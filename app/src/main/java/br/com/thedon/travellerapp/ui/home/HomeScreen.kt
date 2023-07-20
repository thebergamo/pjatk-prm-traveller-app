package br.com.thedon.travellerapp.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.ShareLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.thedon.travellerapp.R
import br.com.thedon.travellerapp.data.firebase.repos.place.Location
import br.com.thedon.travellerapp.data.firebase.repos.place.Place
import br.com.thedon.travellerapp.data.firebase.repos.place.Resources
import br.com.thedon.travellerapp.ui.AppViewModelProvider
import br.com.thedon.travellerapp.ui.TravellerTopAppBar
import br.com.thedon.travellerapp.ui.components.EmptyState
import br.com.thedon.travellerapp.ui.components.ErrorState
import br.com.thedon.travellerapp.ui.components.RemoteImage
import br.com.thedon.travellerapp.ui.navigation.NavigationDestination
import br.com.thedon.travellerapp.ui.theme.TravellerAppTheme
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.auth.api.signin.GoogleSignIn.hasPermissions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.collect
import java.util.concurrent.TimeUnit

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.home_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToPlaceEntry: () -> Unit,
    navigateToPlace: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TravellerTopAppBar(
                title = stringResource(id = HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SmallFloatingActionButton(
                    onClick = { viewModel.toggleMapView() },
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Map,
                        contentDescription = stringResource(R.string.map_action)
                    )

                }
                FloatingActionButton(
                    onClick = navigateToPlaceEntry,
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_action)
                    )

                }

            }
        }
    ) { innerPadding ->
        when(homeUiState.itemList) {
            is Resources.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(align = Alignment.Center)
                )
            }
            is Resources.Success -> {
                if (viewModel.showMapList) {
                    MapBody(
                        itemList = homeUiState.itemList.data ?: emptyList(),
                        onItemClick = navigateToPlace,
                        modifier = modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                } else {
                    HomeBody(
                        itemList = homeUiState.itemList.data ?: emptyList(),
                        onItemClick = navigateToPlace,
                        modifier = modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
            else -> {
                ErrorState(text = homeUiState.itemList.throwable?.localizedMessage ?: "Unkown Error")
            }
        }

    }
}


@Composable
private fun HomeBody(
    itemList: List<Place>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var verticalArrangement = Arrangement.Top
    if (itemList.isEmpty()) {
        verticalArrangement = Arrangement.Center
    }
    Column(
        verticalArrangement = verticalArrangement,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(id = R.dimen.padding_small)),
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logo),
                modifier = Modifier.size(46.dp),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = stringResource(id = R.string.logo_description)
            )

        }
        if (itemList.isEmpty()) {
            EmptyState(
                text = stringResource(R.string.no_places_description),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            PlacesList(
                itemList = itemList,
                onItemClick = { onItemClick(it.id) },
                modifier = Modifier.padding(
                    horizontal = dimensionResource(id = R.dimen.padding_small),
                    vertical = dimensionResource(id = R.dimen.padding_large)
                )
            )
        }
    }
//    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PlacesList(
    itemList: List<Place>,
    onItemClick: (Place) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = itemList, key = { it.id }) { item ->
            PlaceItem(
                item = item,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .combinedClickable(
                        onClick = { onItemClick(item) },
                    )
            )
        }
    }
}

@Composable
private fun PlaceItem(
    item: Place, modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column {
            // Image
            item.photoUrl?.let { url ->
                RemoteImage(
                    url = url,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            // Content
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                // Name
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))
                // Diameter
                Row(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .align(Alignment.End)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    // Icon
                    Icon(
                        imageVector = Icons.Outlined.ShareLocation,
                        contentDescription = "Diameter",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    // Diameter Text
                    Text(
                        text = "${item.diameter}",
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                }

            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    TravellerAppTheme {
        HomeBody(listOf(
            Place(
                "1",
                "São Paulo",
                10.0,
                "",
                "",
                Location(10.0, 10.0),
                "userId"
            )
        ), onItemClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyListPreview() {
    TravellerAppTheme {
        HomeBody(listOf(), onItemClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun DebtorItemPreview() {
    TravellerAppTheme {
        PlaceItem(
            Place(
                "1",
                "São Paulo",
                10.0,
                "",
                "",
                Location(10.0, 10.0),
                "userId"
            )
        )
    }
}
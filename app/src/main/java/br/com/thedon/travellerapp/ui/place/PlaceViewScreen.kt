package br.com.thedon.travellerapp.ui.place

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.thedon.travellerapp.R
import br.com.thedon.travellerapp.data.firebase.repos.place.Place
import br.com.thedon.travellerapp.data.firebase.repos.place.Resources
import br.com.thedon.travellerapp.ui.AppViewModelProvider
import br.com.thedon.travellerapp.ui.TravellerTopAppBar
import br.com.thedon.travellerapp.ui.components.ConfirmDialog
import br.com.thedon.travellerapp.ui.components.ErrorState
import br.com.thedon.travellerapp.ui.components.PlaceRemovalDialog
import br.com.thedon.travellerapp.ui.components.RemoteImage
import br.com.thedon.travellerapp.ui.home.HomeDestination
import br.com.thedon.travellerapp.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object PlaceViewDestination : NavigationDestination {
    override val route = "place_view"
    override val titleRes = R.string.place_view_title
    const val placeIdArg = "placeId"
    val routeWithArgs = "${PlaceViewDestination.route}/{$placeIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceViewScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToEditPlace: (String) -> Unit,
    viewModel: PlaceViewViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val coroutineScope = rememberCoroutineScope()
    val place = viewModel.viewUiState.place
    val isPlaceLoaded = place.data?.name?.isNotBlank() ?: false

    LaunchedEffect(key1 = Unit) {
        viewModel.loadPlace()
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TravellerTopAppBar(
                title = place.data?.name
                    ?: stringResource(id = HomeDestination.titleRes),
                canNavigateBack = true,
                scrollBehavior = scrollBehavior,
                navigateUp = navigateBack,
            )
        },
        floatingActionButton = {
            if (isPlaceLoaded) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SmallFloatingActionButton(
                        onClick = { viewModel.showRemoveDialog() },
                        shape = MaterialTheme.shapes.medium,
                        containerColor = MaterialTheme.colorScheme.error
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.map_action)
                        )

                    }
                    FloatingActionButton(
                        onClick = { navigateToEditPlace(viewModel.placeId) },
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.add_action)
                        )

                    }

                }
            }

        }
    ) { innerPadding ->
        when(place) {
            is Resources.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(align = Alignment.Center)
                )
            }
            is Resources.Success -> {
                PlaceRemovalDialog(
                    show = viewModel.confirmDialog,
                    onDismiss = viewModel::hideRemoveDialog,
                    onConfirm = {
                        // Note: If the user rotates the screen very fast, the operation may get cancelled
                        // and the item may not be updated in the Database. This is because when config
                        // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                        // be cancelled - since the scope is bound to composition.
                        coroutineScope.launch {
                            viewModel.removePlace()
                            navigateBack()
                        }
                    }
                )
                place.data?.let { PlaceBody(place = it, modifier = Modifier.padding(innerPadding)) }
            }
            else -> {
                ErrorState(text = place.throwable?.localizedMessage ?: "Unkown Error")
            }
        }
    }
}

@Composable
fun PlaceBody(
    place: Place,
    modifier: Modifier = Modifier,
) {
    var descriptionText = place.description;

    if (place.description.isNullOrBlank()) {
        descriptionText = "No description provided. Edit this place to add one"
    }

    Column(modifier = modifier.padding(16.dp)) {
        RemoteImage(
            url = place.photoUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(bottom = 8.dp)
        )
        Text(
            text = place.name,
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = descriptionText,
            style = MaterialTheme.typography.bodyMedium
        )
    }

}
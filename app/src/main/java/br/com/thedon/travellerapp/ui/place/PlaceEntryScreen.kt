package br.com.thedon.travellerapp.ui.place

import android.media.ImageReader
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.thedon.travellerapp.R
import br.com.thedon.travellerapp.ui.AppViewModelProvider
import br.com.thedon.travellerapp.ui.TravellerTopAppBar
import br.com.thedon.travellerapp.ui.components.ImagePickerField
import br.com.thedon.travellerapp.ui.components.LocationPickerField
import br.com.thedon.travellerapp.ui.navigation.NavigationDestination
import br.com.thedon.travellerapp.ui.theme.TravellerAppTheme
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.BuildConfig
import kotlinx.coroutines.launch
import java.util.Objects

object PlaceEntryDestination : NavigationDestination {
    override val route = "place_entry"
    override val titleRes = R.string.place_entry_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    viewModel: PlaceEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TravellerTopAppBar(
                title = stringResource(PlaceEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                scrollBehavior = scrollBehavior
            )
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Note: If the user rotates the screen very fast, the operation may get cancelled
                    // and the item may not be saved in the Database. This is because when config
                    // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                    // be cancelled - since the scope is bound to composition.
                    coroutineScope.launch {
                        viewModel.savePlace()
                        navigateBack()
                    }
                },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                if (viewModel.isSubmitting) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = stringResource(R.string.save_action)
                    )
                }


            }
        }) { innerPadding ->
        PlaceEntryBody(
            itemUiState = viewModel.uiState,
            onItemValueChange = viewModel::updateUiState,
            enabled = viewModel.isSubmitting.not(),
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
fun PlaceEntryBody(
    itemUiState: PlaceUiState,
    onItemValueChange: (PlaceDetails) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        PlaceInputForm(
            placeDetails = itemUiState.placeDetails,
            onValueChange = onItemValueChange,
            enabled = enabled
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceInputForm(
    placeDetails: PlaceDetails,
    modifier: Modifier = Modifier,
    onValueChange: (PlaceDetails) -> Unit = {},
    enabled: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ImagePickerField(enabled = enabled,
            label = "Add a picture",
            value = placeDetails.photoUrl,
            onValueChange = { onValueChange(placeDetails.copy(photoUrl = it)) })
        OutlinedTextField(
            value = placeDetails.name,
            onValueChange = { onValueChange(placeDetails.copy(name = it)) },
            label = { Text(stringResource(R.string.place_name_req)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = placeDetails.diameter,
            onValueChange = { onValueChange(placeDetails.copy(diameter = it)) },
            label = { Text(stringResource(R.string.place_diameter_req)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            supportingText = { Text(text = "Provide diameter in KM (e.g.: 1 means 1km)") }
        )
        OutlinedTextField(
            value = placeDetails.description,
            onValueChange = {
                onValueChange(placeDetails.copy(description = it.take(500)))
            },
            label = { Text(stringResource(R.string.place_description_req)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            enabled = enabled,
            singleLine = false,
            maxLines = 4,
            supportingText = {
                Text(
                    text = "${placeDetails.description.length} / 500",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }
        )
        LocationPickerField(
            enabled = enabled,
            label = "Location",
            value = placeDetails.location,
            radius = placeDetails.diameter.toDoubleOrNull(),
            modifier = Modifier.height(280.dp),
            onValueChange = { onValueChange(placeDetails.copy(location = it)) }
        )


    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceEntryScreenPreview() {
    TravellerAppTheme {
        PlaceEntryBody(
            itemUiState = PlaceUiState(
                PlaceDetails(
                    name = "Place name"
                )
            ),
            onItemValueChange = {},
        )
    }
}
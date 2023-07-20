package br.com.thedon.travellerapp.ui.place

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.thedon.travellerapp.R
import br.com.thedon.travellerapp.data.firebase.repos.place.Resources
import br.com.thedon.travellerapp.ui.AppViewModelProvider
import br.com.thedon.travellerapp.ui.TravellerTopAppBar
import br.com.thedon.travellerapp.ui.components.ConfirmDialog
import br.com.thedon.travellerapp.ui.components.ErrorState
import br.com.thedon.travellerapp.ui.components.PlaceRemovalDialog
import br.com.thedon.travellerapp.ui.navigation.NavigationDestination
import br.com.thedon.travellerapp.ui.theme.TravellerAppTheme
import kotlinx.coroutines.launch

object PlaceEditDestination : NavigationDestination {
    override val route = "place_edit"
    override val titleRes = R.string.edit_place_title
    const val placeIdArg = "placeId"
    val routeWithArgs = "$route/{$placeIdArg}"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlaceEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadPlace()
    }

    Scaffold(
        topBar = {
            TravellerTopAppBar(
                title = stringResource(PlaceEditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (viewModel.editState.place is Resources.Success) {
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
                        onClick = {
                            if (viewModel.isPlaceModified()) {
                                viewModel.showChangesDialog()
                            } else {
                                navigateBack()
                            }
                        },
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large)),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = stringResource(R.string.save_action)
                        )

                    }
                }
            }
        }
    ) { innerPadding ->
        ConfirmDialog(
            show = viewModel.confirmDialog,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = stringResource(R.string.dialog_save_icon_description),
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                )

            },
            content = stringResource(R.string.dialog_save_warning_content),
            dissmissActionText = stringResource(R.string.dialog_save_action_no),
            confirmActionText = stringResource(R.string.dialog_save_action_yes),
            onDismiss = viewModel::hideChangesDialog,
            onConfirm = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the item may not be updated in the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    viewModel.updatePlace()
                    navigateBack()
                }
            }
        )
        PlaceRemovalDialog(
            show = viewModel.removeDialog,
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
        when(viewModel.editState.place) {
            is Resources.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(align = Alignment.Center)
                )
            }
            is Resources.Success -> {
                PlaceEntryBody(
                    itemUiState = viewModel.uiState,
                    onItemValueChange = viewModel::updateUiState,
                    modifier = modifier.padding(innerPadding)
                )
            }
            else -> {
                ErrorState(text = viewModel.editState.place.throwable?.localizedMessage ?: "Unkown Error")
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PlaceEditRoutePreview() {
    TravellerAppTheme {
        PlaceEditScreen(navigateBack = { /*Do nothing*/ }, onNavigateUp = { /*Do nothing*/ })
    }
}
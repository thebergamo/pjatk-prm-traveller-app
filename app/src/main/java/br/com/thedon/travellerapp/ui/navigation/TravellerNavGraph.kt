package br.com.thedon.travellerapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import br.com.thedon.travellerapp.ui.AppUiState
import br.com.thedon.travellerapp.ui.AppViewModel
import br.com.thedon.travellerapp.ui.AppViewModelProvider
import br.com.thedon.travellerapp.ui.LoadingDestination
import br.com.thedon.travellerapp.ui.LoadingScreen
import br.com.thedon.travellerapp.ui.home.HomeDestination
import br.com.thedon.travellerapp.ui.home.HomeScreen
import br.com.thedon.travellerapp.ui.login.LoginDestination
import br.com.thedon.travellerapp.ui.login.LoginScreen
import br.com.thedon.travellerapp.ui.place.PlaceEditDestination
import br.com.thedon.travellerapp.ui.place.PlaceEditScreen
import br.com.thedon.travellerapp.ui.place.PlaceEntryDestination
import br.com.thedon.travellerapp.ui.place.PlaceEntryScreen
import br.com.thedon.travellerapp.ui.place.PlaceViewDestination
import br.com.thedon.travellerapp.ui.place.PlaceViewScreen

@Composable
fun TravellerNavHost(
    navController: NavHostController, modifier: Modifier = Modifier,
    viewModel: AppViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        viewModel.uiState.collect { state ->
            when (state) {
                AppUiState.LoggedIn -> navController.navigate("main")
                AppUiState.LoggedOut -> navController.navigate("auth")
                AppUiState.Loading -> { /* no op */
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = LoadingDestination.route,
        modifier = modifier
    ) {
        composable(route = LoadingDestination.route) {
            LoadingScreen()
        }
        navigation(startDestination = LoginDestination.route, route = "auth") {
            composable(route = LoginDestination.route) {
                LoginScreen()
            }
        }
        navigation(startDestination = HomeDestination.route, route = "main") {
            composable(route = HomeDestination.route) {
                HomeScreen(
                    navigateToPlace = { navController.navigate("${PlaceViewDestination.route}/${it}")},
                    navigateToPlaceEntry = { navController.navigate(PlaceEntryDestination.route)}
                )
            }
            composable(
                route = PlaceViewDestination.routeWithArgs,
                arguments = listOf(navArgument(PlaceViewDestination.placeIdArg) {
                    type = NavType.StringType
                })
            ) {
                PlaceViewScreen(
                    navigateBack = { navController.popBackStack() },
                    navigateToEditPlace = { navController.navigate("${PlaceEditDestination.route}/${it}") })
            }
            composable(route = PlaceEntryDestination.route) {
                PlaceEntryScreen(
                    navigateBack = { navController.popBackStack() },
                    onNavigateUp = { navController.navigateUp() })
            }
            composable(
                route = PlaceEditDestination.routeWithArgs,
                arguments = listOf(navArgument(PlaceEditDestination.placeIdArg) {
                    type = NavType.StringType
                })
            ) {
                PlaceEditScreen(
                    navigateBack = { navController.popBackStack() },
                    onNavigateUp = { navController.navigateUp() })
            }
        }
    }
}
package br.com.thedon.travellerapp.ui.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.thedon.travellerapp.R
import br.com.thedon.travellerapp.ui.AppViewModelProvider
import br.com.thedon.travellerapp.ui.navigation.NavigationDestination

object LoginDestination: NavigationDestination {
    override val route = "login"
    override val titleRes = R.string.login_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val isUserLogged by viewModel.isUserLogged.collectAsState()
    val loginLauncher = rememberLauncherForActivityResult(
        viewModel.buildLoginActivityResult()
    ) { result ->
        if (result != null) {
            viewModel.onLoginResult(result = result)
        }
    }

    if (!isUserLogged) {
        LaunchedEffect(true)  {
            loginLauncher.launch(viewModel.buildLoginIntent())
        }
    }
}
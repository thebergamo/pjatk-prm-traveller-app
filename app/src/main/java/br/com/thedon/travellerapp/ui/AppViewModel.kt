package br.com.thedon.travellerapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.thedon.travellerapp.data.firebase.repos.user.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AppViewModel(private val userRepository: UserRepository): ViewModel() {
    val uiState: StateFlow<AppUiState> = userRepository.isSignedIn.map {
        when(it) {
            true -> AppUiState.LoggedIn
            false -> AppUiState.LoggedOut
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppUiState.Loading)
}

sealed interface AppUiState {
    object Loading: AppUiState
    object LoggedIn: AppUiState
    object LoggedOut: AppUiState
}
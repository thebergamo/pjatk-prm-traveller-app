package br.com.thedon.travellerapp.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.thedon.travellerapp.TravellerApplication
import br.com.thedon.travellerapp.ui.home.HomeViewModel
import br.com.thedon.travellerapp.ui.login.LoginViewModel
import br.com.thedon.travellerapp.ui.place.PlaceEditViewModel
import br.com.thedon.travellerapp.ui.place.PlaceEntryViewModel
import br.com.thedon.travellerapp.ui.place.PlaceViewViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Traveller app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            AppViewModel(
                travellerApplication().container.userRepository
            )
        }
        initializer {
            LoginViewModel(
                travellerApplication().container.userRepository
            )
        }

        initializer {
            HomeViewModel(
                travellerApplication().container.placesRepository,
                travellerApplication().container.geofenceRepository
            )
        }

        initializer {
            PlaceEntryViewModel(
                travellerApplication().container.placesRepository
            )
        }

        initializer {
            PlaceEditViewModel(
                this.createSavedStateHandle(),
                travellerApplication().container.placesRepository
            )
        }

        initializer {
            PlaceViewViewModel(
                this.createSavedStateHandle(),
                travellerApplication().container.placesRepository
            )
        }


    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [TravellerApplication].
 */
fun CreationExtras.travellerApplication(): TravellerApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TravellerApplication)
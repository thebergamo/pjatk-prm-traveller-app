package br.com.thedon.travellerapp.ui.home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.thedon.travellerapp.data.firebase.repos.GeofenceRepository
import br.com.thedon.travellerapp.data.firebase.repos.place.Place
import br.com.thedon.travellerapp.data.firebase.repos.place.PlacesRepository
import br.com.thedon.travellerapp.data.firebase.repos.place.Resources
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val placesRepository: PlacesRepository,
    private val geofenceRepository: GeofenceRepository
) : ViewModel() {
    var showMapList by mutableStateOf(false)
        private set

    private val _uiState = placesRepository.getUserPlaces.map {
        if (it.data != null) {
            Log.i("HOME", "Add to GEO")
            geofenceRepository.addPlaces(it.data)
        }
        HomeUiState(itemList = it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())


    val homeUiState: StateFlow<HomeUiState>
        get() = _uiState

    fun toggleMapView() {
        showMapList = showMapList.not()
    }

}

data class HomeUiState(
    val itemList: Resources<List<Place>> = Resources.Loading()
)
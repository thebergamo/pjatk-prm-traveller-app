package br.com.thedon.travellerapp.ui.place

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.thedon.travellerapp.data.firebase.repos.place.Place
import br.com.thedon.travellerapp.data.firebase.repos.place.PlacesRepository
import br.com.thedon.travellerapp.data.firebase.repos.place.Resources
import br.com.thedon.travellerapp.ui.home.HomeUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaceViewViewModel(
    savedStateHandle: SavedStateHandle,
    private val placesRepository: PlacesRepository
) : ViewModel() {
    val placeId: String = checkNotNull(savedStateHandle[PlaceEditDestination.placeIdArg])

    var confirmDialog by mutableStateOf(false)
        private set

    private val _place = mutableStateOf(ViewUiState())

    val viewUiState: ViewUiState
        get() = _place.value

    suspend fun loadPlace() {
        placesRepository.getPlace(placeId).let {
            _place.value = ViewUiState(place = it)
        }
    }

    suspend fun removePlace() {
        placesRepository.removePlace(placeId)
        hideRemoveDialog()
    }

    fun showRemoveDialog() {
        confirmDialog = true;
    }

    fun hideRemoveDialog() {
        confirmDialog = false
    }

}

data class ViewUiState(
    val place: Resources<Place> = Resources.Loading()
)
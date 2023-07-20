package br.com.thedon.travellerapp.ui.place

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.thedon.travellerapp.data.firebase.repos.place.Place
import br.com.thedon.travellerapp.data.firebase.repos.place.PlacesRepository
import br.com.thedon.travellerapp.data.firebase.repos.place.Resources
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaceEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val placesRepository: PlacesRepository
) : ViewModel() {
    /**
     * Holds current item ui state
     */
    var uiState by mutableStateOf(PlaceUiState())
        private set

    var editState by mutableStateOf(EditUiState())

    private val placeId: String = checkNotNull(savedStateHandle[PlaceEditDestination.placeIdArg])

    private var savedPlace: Place? = null

    var confirmDialog by mutableStateOf(false)
        private set

    var removeDialog by mutableStateOf(false)
        private set

    suspend fun loadPlace() {
        placesRepository.getPlace(placeId).let {
            savedPlace = it.data
            editState = EditUiState(it)
            uiState = it.data?.toPlaceUiState(true) ?: PlaceUiState()
        }
    }

    suspend fun updatePlace() {
        if (validateInput(uiState.placeDetails)) {
            val place = uiState.placeDetails.toPlace()
            placesRepository.savePlace(place.copy(id = placeId))
            hideChangesDialog()
        }
    }

    suspend fun removePlace() {
        placesRepository.removePlace(placeId)
        hideRemoveDialog()
    }

    fun showRemoveDialog() {
        removeDialog = true;
    }

    fun hideRemoveDialog() {
        removeDialog = false
    }

    fun isPlaceModified(): Boolean {
        val place = uiState.placeDetails.toPlace();
        return place.name != savedPlace?.name
                || place.diameter != savedPlace?.diameter
                || place.photoUrl != savedPlace?.photoUrl
                || place.location?.longitude != savedPlace?.location?.longitude
                || place.location?.latitude != savedPlace?.location?.latitude
                || place.description != savedPlace?.description
    }

    fun updateUiState(placeDetails: PlaceDetails) {
        uiState =
            PlaceUiState(placeDetails = placeDetails, isEntryValid = validateInput(placeDetails))
    }

    fun showChangesDialog() {
        confirmDialog = true;
    }

    fun hideChangesDialog() {
        confirmDialog = false
    }

    private fun validateInput(placeUiState: PlaceDetails = this.uiState.placeDetails): Boolean {
        return with(placeUiState) {
            return name.isNotBlank() && photoUrl.isNotBlank() && location.isNotBlank()
        }
    }
}

data class EditUiState(
    val place: Resources<Place> = Resources.Loading()
)
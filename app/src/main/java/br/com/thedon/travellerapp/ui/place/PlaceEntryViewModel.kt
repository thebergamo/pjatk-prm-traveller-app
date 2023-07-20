package br.com.thedon.travellerapp.ui.place

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import br.com.thedon.travellerapp.data.firebase.repos.place.Location
import br.com.thedon.travellerapp.data.firebase.repos.place.Place
import br.com.thedon.travellerapp.data.firebase.repos.place.PlacesRepository

class PlaceEntryViewModel(private val placesRepository: PlacesRepository) : ViewModel() {
    var uiState by mutableStateOf(PlaceUiState())
        private set

    var isSubmitting by mutableStateOf(false)
        private set

    fun updateUiState(placeDetails: PlaceDetails) {
        uiState = PlaceUiState(placeDetails = placeDetails, isEntryValid = false)
    }

    suspend fun savePlace() {
        isSubmitting = true
        if (validateInput()) {
            placesRepository.savePlace(uiState.placeDetails.toPlace())
            isSubmitting = false
        }
    }

    private fun validateInput(placeUiState: PlaceDetails = uiState.placeDetails): Boolean {
        return with(placeUiState) {
            return name.isNotBlank() && photoUrl.isNotBlank() && location.isNotBlank()
        }
    }
}

fun Location.isNotBlank(): Boolean {
    return !latitude.equals(null) && latitude != 0.0
            && !longitude.equals(null) && longitude != 0.0
}


data class PlaceUiState(val placeDetails: PlaceDetails = PlaceDetails(), val isEntryValid: Boolean = false)

data class PlaceDetails(
    val name: String = "",
    val diameter: String = "1.0",
    val photoUrl: String = "",
    val description: String = "",
    val location: Location = Location(0.0, 0.0),
)

/**
 * Extension function to convert [PlaceUiState] to [Place].
 */
fun PlaceDetails.toPlace(): Place = Place(
    name = name,
    diameter = diameter.toDoubleOrNull() ?: 1.0,
    photoUrl = photoUrl,
    description = description,
    location = location
)

/**
 * Extension function to convert [Place] to [PlaceUiState]
 */
fun Place.toPlaceUiState(isEntryValid: Boolean = false): PlaceUiState = PlaceUiState(
    placeDetails = this.toPlaceDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Place] to [PlaceDetails]
 */
fun Place.toPlaceDetails(): PlaceDetails = PlaceDetails(
    name = name,
    diameter = diameter.toString(),
    photoUrl = photoUrl,
    description = description,
    location = location
)
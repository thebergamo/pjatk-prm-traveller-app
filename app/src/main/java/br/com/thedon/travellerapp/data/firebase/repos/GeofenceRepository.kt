package br.com.thedon.travellerapp.data.firebase.repos


import br.com.thedon.travellerapp.data.firebase.repos.place.Place

interface GeofenceRepository {
    suspend fun addPlace(place: Place)
    suspend fun addPlaces(places: List<Place>)
}
package br.com.thedon.travellerapp.data.firebase.repos.place

import com.google.firebase.firestore.DocumentId

data class Location(val latitude: Double = 0.0, val longitude: Double = 0.0)
data class Place(
    @DocumentId val id: String = "",
    val name: String = "",
    val diameter: Double = 1.0,
    val photoUrl: String = "",
    val description: String = "",
    val location: Location = Location(),
    val userId: String = ""
)

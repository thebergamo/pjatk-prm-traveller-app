package br.com.thedon.travellerapp.data

import android.content.Context
import br.com.thedon.travellerapp.data.firebase.repos.GeofenceRepository
import br.com.thedon.travellerapp.data.firebase.repos.GeofenceRepositoryImpl
import br.com.thedon.travellerapp.data.firebase.repos.place.PlaceRepositoryImpl
import br.com.thedon.travellerapp.data.firebase.repos.place.PlacesRepository
import br.com.thedon.travellerapp.data.firebase.repos.user.UserRepository
import br.com.thedon.travellerapp.data.firebase.repos.user.UserRepositoryImpl
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

interface AppContainer {
    val userRepository: UserRepository
    val placesRepository: PlacesRepository
    val geofenceRepository: GeofenceRepository
}

class AppDataContainer(private val context: Context): AppContainer {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    override val userRepository: UserRepository by lazy {
        UserRepositoryImpl(auth)
    }
    override val placesRepository: PlacesRepository by lazy {
        PlaceRepositoryImpl(firestore, auth, storage)
    }

    override val geofenceRepository: GeofenceRepository by lazy {
        GeofenceRepositoryImpl(geofencingClient, context)
    }

}
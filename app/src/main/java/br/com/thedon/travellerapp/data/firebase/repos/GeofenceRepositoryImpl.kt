package br.com.thedon.travellerapp.data.firebase.repos

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import br.com.thedon.travellerapp.GeofenceBroadcastReceiver
import br.com.thedon.travellerapp.data.firebase.repos.place.Place
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
class GeofenceRepositoryImpl(
    private val geofenceClient: GeofencingClient,
    private val context: Context
) : GeofenceRepository {

    override suspend fun addPlace(place: Place) {
        val geofence = Geofence.Builder()
            .setRequestId(place.id)
            .setCircularRegion(
                place.location.latitude,
                place.location.longitude,
                place.diameter.times(1000).toFloat()
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or
                        Geofence.GEOFENCE_TRANSITION_EXIT
            )
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val geofencePendingIntent: PendingIntent by lazy {
            val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
        }

        geofenceClient.addGeofences(geofencingRequest, geofencePendingIntent).addOnSuccessListener {
            Log.i("GEO", "Great success! - ${place.name}")
        }.addOnFailureListener {
            Log.e("GEO", "Failed - ${it.message}")
        }
    }

    override suspend fun addPlaces(places: List<Place>) {
        places.map {
            addPlace(it)
        }
    }
}

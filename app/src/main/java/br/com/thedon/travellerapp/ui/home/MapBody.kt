package br.com.thedon.travellerapp.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import br.com.thedon.travellerapp.data.firebase.repos.place.Place
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.concurrent.TimeUnit

@SuppressLint("MissingPermission")
@Composable
fun getUserLocation(locationProvider: FusedLocationProviderClient): LatLng {
    var currentUserLocation by remember { mutableStateOf(LatLng(54.4052799, 18.6274038)) }

    DisposableEffect(key1 = locationProvider) {
        val locationCallback = object : LocationCallback() {
            //1
            override fun onLocationResult(result: LocationResult) {
                /**
                 * Option 1
                 * This option returns the locations computed, ordered from oldest to newest.
                 * */
                /**
                 * Option 1
                 * This option returns the locations computed, ordered from oldest to newest.
                 * */
                for (location in result.locations) {
                    // Update data class with location data
                    currentUserLocation = LatLng(location.latitude, location.longitude)

                }


                /**
                 * Option 2
                 * This option returns the most recent historical location currently available.
                 * Will return null if no historical location is available
                 * */


                /**
                 * Option 2
                 * This option returns the most recent historical location currently available.
                 * Will return null if no historical location is available
                 * */
                locationProvider.lastLocation
                    .addOnSuccessListener { location ->
                        location?.let {
                            // Update data class with location data
                            currentUserLocation = LatLng(location.latitude, location.longitude)
                            Log.w(
                                "MAP_LOCATION",
                                "Current location: ${location.latitude}, ${location.latitude}"
                            )
                        }
                    }.addOnFailureListener {
                        Log.e("Location_error", "${it.message}")
                    }

            }
        }

        locationProvider.requestLocationUpdates(
            LocationRequest.Builder(TimeUnit.SECONDS.toMillis(60)).build(),
            locationCallback,
            Looper.getMainLooper()
        )

        onDispose {
            locationProvider.removeLocationUpdates(locationCallback)
        }
    }
    //4
    return currentUserLocation
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapBody(itemList: List<Place>, onItemClick: (String) -> Unit, modifier: Modifier) {
    val context = LocalContext.current
    val locationProvider = LocationServices.getFusedLocationProviderClient(context)
    val multiplePermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    LaunchedEffect(Unit) {
        multiplePermissionState.launchMultiplePermissionRequest()
    }
    val currentValue = getUserLocation(locationProvider)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentValue, 15f)
    }
    val isMyLocationEnabled = multiplePermissionState.allPermissionsGranted

    LaunchedEffect(currentValue) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(currentValue, 15f)
    }

    GoogleMap(
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = isMyLocationEnabled),
        uiSettings = MapUiSettings(compassEnabled = true),
        modifier = modifier
    ) {
        itemList.map { place ->
            val position = LatLng(place.location.latitude, place.location.longitude)
            Circle(
                center = position,
                radius = (place.diameter ?: 1.0) * 1000,
                strokeColor = MaterialTheme.colorScheme.inversePrimary,
                strokeWidth = 2f,
                fillColor = MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.4f),
            )
            Marker(
                state = MarkerState(position = position),
                title = place.name,
                snippet = "Diameter: ${place.diameter}",
                onInfoWindowClick = {
                    onItemClick(place.id)
                }
            )
        }

        MarkerInfoWindow() { marker ->
            Column {
                Text(marker.title!!)
                Text(marker.snippet!!)
                Text("Click to visit", style = MaterialTheme.typography.bodySmall)
            }
        }


    }
}
package br.com.thedon.travellerapp.ui.components

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.thedon.travellerapp.data.firebase.repos.place.Location
import br.com.thedon.travellerapp.ui.place.isNotBlank
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPickerField(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    label: String,
    value: Location,
    onValueChange: (Location) -> Unit,
    radius: Double?
) {
    val multiplePermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    var currentValue = LatLng(54.4052799, 18.6274038)

    if (value.isNotBlank()) {
        currentValue = LatLng(value.latitude, value.longitude)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentValue, 10f)
    }

    if (cameraPositionState.isMoving) {
        onValueChange(
            Location(
                latitude = cameraPositionState.position.target.latitude,
                longitude = cameraPositionState.position.target.longitude
            )
        )
    }

    LaunchedEffect(Unit) {
        multiplePermissionState.launchMultiplePermissionRequest()
    }

    val isMyLocationEnabled = multiplePermissionState.allPermissionsGranted

    Column(modifier = modifier) {
        Text(text = "Location", modifier = Modifier.padding(8.dp))

        GoogleMap(
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = isMyLocationEnabled),
            uiSettings = MapUiSettings(compassEnabled = true)
        ) {
            Circle(
                center = cameraPositionState.position.target,
                radius = (radius ?: 1.0) * 1000,
                strokeColor = MaterialTheme.colorScheme.inversePrimary,
                strokeWidth = 2f,
                fillColor = MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.4f),
            )
            Marker(
                state = MarkerState(position = cameraPositionState.position.target)
            )
        }

    }
}
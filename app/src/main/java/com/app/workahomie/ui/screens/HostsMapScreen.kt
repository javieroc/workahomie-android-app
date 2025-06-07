package com.app.workahomie.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.app.workahomie.data.Host
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun HostsMapScreen(
    hosts: List<Host>,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState {
        val (lng, lat) = hosts.firstOrNull()?.location?.coordinates ?: listOf(0.0, 0.0)
        position = CameraPosition.fromLatLngZoom(
            LatLng(lat, lng), 10f
        )
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        hosts.forEach { host ->
            Marker(
                state = MarkerState(
                    position = LatLng(host.location.coordinates[1], host.location.coordinates[0])
                ),
                title = host.firstName
            )
        }
    }
}

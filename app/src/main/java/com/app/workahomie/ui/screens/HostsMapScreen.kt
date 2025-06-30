package com.app.workahomie.ui.screens

import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.workahomie.data.Host
import com.app.workahomie.models.HostViewModel
import com.app.workahomie.models.MapViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun HostsMapScreen(
    hosts: List<Host>,
    modifier: Modifier = Modifier,
    mapViewModel: MapViewModel = viewModel(),
    hostViewModel: HostViewModel = viewModel()
) {
    val context = LocalContext.current
    val userLocation by mapViewModel.userLocation
    val selectedLocation by hostViewModel.selectedLocation
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val cameraPositionState = rememberCameraPositionState {
        val (lng, lat) = hosts.firstOrNull()?.location?.coordinates ?: listOf(0.0, 0.0)
        position = CameraPosition.fromLatLngZoom(
            LatLng(lat, lng), 10f
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            mapViewModel.fetchUserLocation(context, fusedLocationClient)
        } else {
            Log.e("Error", "Location permission was denied by the user.")
        }
    }

    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                mapViewModel.fetchUserLocation(context, fusedLocationClient)
            }
            else -> {
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    LaunchedEffect(selectedLocation, userLocation) {
        val target = selectedLocation ?: userLocation
        target?.let {
            cameraPositionState.move(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(it, 12f)
                ),
                // durationMs = 1000
            )
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        userLocation?.let {
            Marker(
                state = MarkerState(position = it),
                title = "You are here",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )
        }

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

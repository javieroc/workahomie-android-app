package com.app.workahomie.ui.screens

import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.unit.dp
import com.app.workahomie.data.Host
import com.app.workahomie.models.HostViewModel
import com.app.workahomie.models.MapViewModel
import com.app.workahomie.ui.components.CustomMapMarker
import com.app.workahomie.ui.components.HostMiniCard
import com.google.android.gms.location.LocationServices
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
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val cameraPositionState = rememberCameraPositionState {
        val (lng, lat) = hosts.firstOrNull()?.location?.coordinates ?: listOf(0.0, 0.0)
        position = CameraPosition.fromLatLngZoom(LatLng(lat, lng), 10f)
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

    // --- state for selected host (delegate needs getValue/setValue imports) ---
    var selectedHost by remember { mutableStateOf<Host?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
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
                CustomMapMarker(
                    location = LatLng(host.location.coordinates[1], host.location.coordinates[0]),
                    occupation = host.occupation,
                    onClick = {
                        // open mini card for this host
                        selectedHost = host
                    }
                )
            }
        }

        // If a host is selected, show a tap-to-dismiss scrim and the HostMiniCard
        selectedHost?.let { host ->
            // scrim that dismisses when tapped outside
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        onClick = { selectedHost = null },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            )

            HostMiniCard(
                host = host,
                onClose = { selectedHost = null },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

package com.app.workahomie.ui.screens

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.workahomie.data.Host
import com.app.workahomie.models.MapViewModel
import com.app.workahomie.ui.components.CustomMapMarker
import com.app.workahomie.ui.components.HostBottomSheet
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostsMapScreen(
    hosts: List<Host>,
    modifier: Modifier = Modifier,
    mapViewModel: MapViewModel = viewModel(),
    onHostClick: (Host) -> Unit,
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
            Toast.makeText(context, "Location permission was denied.", Toast.LENGTH_SHORT).show()
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

    var selectedHost by remember { mutableStateOf<Host?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

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
                host.location?.coordinates?.let { coords ->
                    if (coords.size >= 2) {
                        CustomMapMarker(
                            location = LatLng(coords[1], coords[0]),
                            occupation = host.occupation,
                            onClick = {
                                selectedHost = host
                            }
                        )
                    }
                } ?: run {
                    Toast.makeText(context, "Host has no location", Toast.LENGTH_SHORT).show()
                }
            }
        }

        selectedHost?.let { host ->
            HostBottomSheet(
                host = host,
                sheetState = sheetState,
                onDismissRequest = {
                    scope.launch { sheetState.hide() }
                    selectedHost = null
                },
                onHostClick = { onHostClick(host) }
            )
        }
    }
}

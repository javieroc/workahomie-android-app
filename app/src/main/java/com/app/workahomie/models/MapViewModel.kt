package com.app.workahomie.models

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng

class MapViewModel: ViewModel() {
    var userLocation = mutableStateOf<LatLng?>(null)
        private set

    var selectedLocation = mutableStateOf<LatLng?>(null)
        private set

    fun fetchUserLocation(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        userLocation.value = LatLng(it.latitude, it.longitude)
                    }
                }
            } catch (e: SecurityException) {
                Log.e("Error", "Permission for location access was revoked: ${e.localizedMessage}")
            }
        } else {
            Log.e("Error", "Location permission is not granted.")
        }
    }
}

package com.app.workahomie.models

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.workahomie.data.Host
import com.app.workahomie.network.HostApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface HostsUiState {
    data class Success(val hosts: List<Host>) : HostsUiState
    object Error : HostsUiState
    object Loading : HostsUiState
}

class HostViewModel : ViewModel() {
    private var offset = 0
    private val limit = 10

    var isPaginating by mutableStateOf(false)
        private set

    private var isLastPage = false

    private val loadedHosts = mutableListOf<Host>()

    var hostsUiState: HostsUiState by mutableStateOf(HostsUiState.Loading)
        private set

    var isMapView by mutableStateOf(false)
        private set

    var userLocation = mutableStateOf<LatLng?>(null)
        private set

    init {
        loadMoreHosts()
    }

    fun toggleView() {
        isMapView = !isMapView
    }

    fun loadMoreHosts() {
        if (isPaginating || isLastPage) return

        isPaginating = true

        viewModelScope.launch {
            try {
                val response = HostApi.retrofitService.getHosts(offset = offset, limit = limit)
                val newHosts = response.data

                if (newHosts.isEmpty()) {
                    isLastPage = true
                } else {
                    loadedHosts.addAll(newHosts)
                    offset += limit
                }
                hostsUiState = HostsUiState.Success(loadedHosts.toList())
            } catch (e: IOException) {
                hostsUiState = HostsUiState.Error
            } catch (e: HttpException) {
                hostsUiState = HostsUiState.Error
            } finally {
                isPaginating = false
            }
        }
    }

    fun refreshHosts() {
        offset = 0
        isLastPage = false
        loadedHosts.clear()
        hostsUiState = HostsUiState.Loading
        loadMoreHosts()
    }

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

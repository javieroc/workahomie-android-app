package com.app.workahomie.models

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.workahomie.data.CreateRequestDto
import com.app.workahomie.data.Host
import com.app.workahomie.network.HostApi
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface HostsUiState {
    data class Success(val hosts: List<Host>) : HostsUiState
    data class Error(val message: String) : HostsUiState
    object Loading : HostsUiState
}

sealed interface CreateRequestUiState {
    object Success : CreateRequestUiState
    data class Error(val message: String) : CreateRequestUiState
    object Loading : CreateRequestUiState
    object Idle : CreateRequestUiState
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

    var createRequestUiState: CreateRequestUiState by mutableStateOf(CreateRequestUiState.Idle)
        private set

    var isMapView by mutableStateOf(false)
        private set

    var selectedLocation = mutableStateOf<LatLng?>(null)
        private set

    init {
        loadMoreHosts()
    }

    fun toggleView() {
        isMapView = !isMapView
    }

    fun createRequest(
        hostId: String,
        checkIn: String,
        checkOut: String,
        message: String,
        userName: String?,
        userEmail: String?,
        userAvatar: String?
    ) {
        viewModelScope.launch {
            createRequestUiState = CreateRequestUiState.Loading
            try {
                val dto = CreateRequestDto(
                    checkIn = checkIn,
                    checkOut = checkOut,
                    message = message,
                    userName = userName,
                    userEmail = userEmail,
                    userAvatar = userAvatar
                )
                HostApi.retrofitService.createRequest(hostId, dto)
                createRequestUiState = CreateRequestUiState.Success
            } catch (e: Exception) {
                createRequestUiState = CreateRequestUiState.Error("Failed to send request.")
            }
        }
    }

    fun resetCreateRequestState() {
        createRequestUiState = CreateRequestUiState.Idle
    }

    fun loadMoreHosts() {
        if (isPaginating || isLastPage) return

        isPaginating = true

        viewModelScope.launch {
            try {
                val latLng = selectedLocation.value
                val response = HostApi.retrofitService.getHosts(
                    offset = offset,
                    limit = limit,
                    lat = latLng?.latitude,
                    lng = latLng?.longitude
                )
                val newHosts = response.data

                if (newHosts.isEmpty()) {
                    isLastPage = true
                } else {
                    loadedHosts.addAll(newHosts)
                    offset += limit
                }
                hostsUiState = HostsUiState.Success(loadedHosts.toList())
            } catch (e: IOException) {
                hostsUiState = HostsUiState.Error("Network error")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = errorBody ?: e.message()
                Log.e("Error", errorMessage)
                hostsUiState = HostsUiState.Error(errorMessage)
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

    fun fetchLatLngFromPlaceId(placeId: String, context: Context) {
        val placesClient = Places.createClient(context)

        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val latLng = response.place.latLng
                if (latLng != null) {
                    selectedLocation.value = latLng
                } else {
                    Log.e("Error", "LatLng is null for placeId: $placeId")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Error", "Failed to fetch place details")
            }
    }
}

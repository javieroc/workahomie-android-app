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
import com.app.workahomie.network.toMultipartBodyPart
import com.app.workahomie.network.toRequestBodyPart
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.io.IOException

sealed interface HostsUiState {
    data class Success(val hosts: List<Host>) : HostsUiState
    data class Error(val message: String) : HostsUiState
    data object Loading : HostsUiState
}

sealed interface CreateRequestUiState {
    data object Success : CreateRequestUiState
    data class Error(val message: String) : CreateRequestUiState
    data object Loading : CreateRequestUiState
    data object Idle : CreateRequestUiState
}

sealed interface HostDetailsUiState {
    data object Loading : HostDetailsUiState
    data class Success(val host: Host) : HostDetailsUiState
    data class Error(val message: String) : HostDetailsUiState
    data object Idle : HostDetailsUiState
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
            .addOnFailureListener { _ ->
                Log.e("Error", "Failed to fetch place details")
            }
    }

    var hostState = mutableStateOf<HostDetailsUiState>(HostDetailsUiState.Idle)
        private set

    fun fetchHostMe() {
        viewModelScope.launch {
            hostState.value = HostDetailsUiState.Loading
            try {
                val me = HostApi.retrofitService.getHostMe()
                hostState.value = HostDetailsUiState.Success(me)
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    hostState.value = HostDetailsUiState.Success(Host())
                } else {
                    hostState.value = HostDetailsUiState.Error("Server error: ${e.message()}")
                }
            } catch (e: Exception) {
                hostState.value = HostDetailsUiState.Error("Unexpected error: ${e.message}")
            }
        }
    }

    fun saveHost(updatedHost: Host, profileFile: File? = null) {
        viewModelScope.launch {
            try {
                if (updatedHost.id.isEmpty()) {
                    // New host → create
                    val profilePart = profileFile?.toMultipartBodyPart("profile")
                    val createdHost = HostApi.retrofitService.createHost(
                        firstName = updatedHost.firstName.toRequestBodyPart(),
                        lastName = updatedHost.lastName.toRequestBodyPart(),
                        occupation = updatedHost.occupation.toRequestBodyPart(),
                        aboutMe = updatedHost.aboutMe.toRequestBodyPart(),
                        phone = updatedHost.phone?.toRequestBodyPart(),
                        profile = profilePart
                    )
                    hostState.value = HostDetailsUiState.Success(createdHost)
                } else {
                    // Existing host → update profile
                    val profilePart = profileFile?.toMultipartBodyPart("profile")
                    val updated = HostApi.retrofitService.updateHostMe(
                        firstName = updatedHost.firstName.toRequestBodyPart(),
                        lastName = updatedHost.lastName.toRequestBodyPart(),
                        occupation = updatedHost.occupation.toRequestBodyPart(),
                        aboutMe = updatedHost.aboutMe.toRequestBodyPart(),
                        phone = updatedHost.phone?.toRequestBodyPart(),
                        profile = profilePart
                    )
                    hostState.value = HostDetailsUiState.Success(updated)
                }
            } catch (e: Exception) {
                hostState.value = HostDetailsUiState.Error("Failed to save host: ${e.message}")
            }
        }
    }

    fun updateHostPlace(updatedHost: Host, pictureFiles: List<File> = emptyList()) {
        viewModelScope.launch {
            hostState.value = HostDetailsUiState.Loading
            try {
                val pictureParts = pictureFiles.map { it.toMultipartBodyPart("pictures") }
                val facilityParts = updatedHost.facilities.map { it.toRequestBodyPart() }

                val result = HostApi.retrofitService.updateHostPlace(
                    address = updatedHost.address.toRequestBodyPart(),
                    placeDescription = updatedHost.placeDescription.toRequestBodyPart(),
                    placeDetails = updatedHost.placeDetails.toRequestBodyPart(),
                    facilities = facilityParts,
                    pictures = pictureParts
                )

                hostState.value = HostDetailsUiState.Success(result)
                Log.d("HostViewModel", "Place updated successfully: ${result.id}")
            } catch (e: Exception) {
                Log.e("HostViewModel", "Failed to update host place", e)
                hostState.value = HostDetailsUiState.Error("Failed to update place")
            }
        }
    }
}

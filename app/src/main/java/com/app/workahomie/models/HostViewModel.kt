package com.app.workahomie.models

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.workahomie.data.CreateRequestDto
import com.app.workahomie.data.Host
import com.app.workahomie.data.HostFilters
import com.app.workahomie.network.HostApi
import com.app.workahomie.utils.toMultipartBodyPart
import com.app.workahomie.utils.toMultipartBodyParts
import com.app.workahomie.utils.toRequestBodyPart
import com.app.workahomie.utils.uriToFile
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
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

    var hostFilters by mutableStateOf(HostFilters())
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
                    lng = latLng?.longitude,
                    occupations = hostFilters.occupations.ifEmpty { null },
                    facilities = hostFilters.facilities.ifEmpty { null },
                    rate = hostFilters.rate
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
                    Toast.makeText(context, "Could not find location for the selected address", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { _ ->
                Toast.makeText(context, "Failed to fetch place details", Toast.LENGTH_SHORT).show()
            }
    }

    fun applyFilters(filters: HostFilters) {
        hostFilters = filters
        refreshHosts()
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

    fun saveHost(updatedHost: Host, profileUri: Uri?, context: Context) {
        viewModelScope.launch {
            try {
                val profileFile = profileUri?.let { uriToFile(context, it, "profile_${updatedHost.userId}.jpg") }

                val responseHost = if (updatedHost.id.isEmpty()) {
                    // Create host (if applicable)
                    HostApi.retrofitService.createHost(
                        firstName = updatedHost.firstName.toRequestBodyPart(),
                        lastName = updatedHost.lastName.toRequestBodyPart(),
                        occupation = updatedHost.occupation.toRequestBodyPart(),
                        aboutMe = updatedHost.aboutMe.toRequestBodyPart(),
                        phone = updatedHost.phone?.toRequestBodyPart(),
                        profile = profileFile?.toMultipartBodyPart("profile")
                    )
                } else {
                    // Update existing host with file
                    HostApi.retrofitService.updateHostMe(
                        firstName = updatedHost.firstName.toRequestBodyPart(),
                        lastName = updatedHost.lastName.toRequestBodyPart(),
                        occupation = updatedHost.occupation.toRequestBodyPart(),
                        aboutMe = updatedHost.aboutMe.toRequestBodyPart(),
                        phone = updatedHost.phone?.toRequestBodyPart(),
                        profile = profileFile?.toMultipartBodyPart("profile")
                    )
                }

                hostState.value = HostDetailsUiState.Success(responseHost)
            } catch (e: Exception) {
                hostState.value = HostDetailsUiState.Error("Failed to save host: ${e.message}")
            }
        }
    }

    fun updateHostPlace(
        updatedHost: Host,
        existingPictures: List<String>,
        newPictureUris: List<Uri>,
        context: Context
    ) {
        viewModelScope.launch {
            hostState.value = HostDetailsUiState.Loading
            try {
                val addressPart = updatedHost.address.toRequestBody("text/plain".toMediaTypeOrNull())
                val placeDescriptionPart = updatedHost.placeDescription.toRequestBody("text/plain".toMediaTypeOrNull())
                val placeDetailsPart = updatedHost.placeDetails.toRequestBody("text/plain".toMediaTypeOrNull())

                val facilityParts = updatedHost.facilities.toMultipartBodyParts("facilities")
                val existingPicturesParts = existingPictures.toMultipartBodyParts("existingPictures")

                val pictureParts = newPictureUris.mapIndexed { index, uri ->
                    val file = uriToFile(context, uri, "place_${index}_${updatedHost.userId}.jpg")
                    file.toMultipartBodyPart("pictures")
                }

                val result = HostApi.retrofitService.updateHostPlace(
                    address = addressPart,
                    placeDescription = placeDescriptionPart,
                    placeDetails = placeDetailsPart,
                    facilities = facilityParts,
                    existingPictures = existingPicturesParts,
                    pictures = pictureParts
                )

                hostState.value = HostDetailsUiState.Success(result)

            } catch (e: Exception) {
                hostState.value = HostDetailsUiState.Error("Failed to update place")
            }
        }
    }
}

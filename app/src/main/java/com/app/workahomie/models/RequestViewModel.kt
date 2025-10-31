package com.app.workahomie.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.workahomie.data.Request
import com.app.workahomie.data.RequestStatus
import com.app.workahomie.data.UpdateRequestStatusDto
import com.app.workahomie.network.HostApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class RequestsData(
    val incoming: List<Request>,
    val outgoing: List<Request>
)

sealed interface RequestsUiState {
    data class Success(val data: RequestsData) : RequestsUiState
    data class Error(val message: String) : RequestsUiState
    data object Loading : RequestsUiState
}

class RequestViewModel : ViewModel() {
    var requestsUiState: RequestsUiState by mutableStateOf(RequestsUiState.Loading)
        private set

    init {
        getRequests()
    }

    private fun getRequests() {
        viewModelScope.launch {
            requestsUiState = RequestsUiState.Loading
            try {
                val incomingDeferred = async { HostApi.retrofitService.getIncomingRequests() }
                val outgoingDeferred = async { HostApi.retrofitService.getOutgoingRequests() }

                val incomingResponse = incomingDeferred.await()
                val outgoingResponse = outgoingDeferred.await()

                requestsUiState = RequestsUiState.Success(
                    RequestsData(
                        incoming = incomingResponse.data,
                        outgoing = outgoingResponse.data
                    )
                )
            } catch (e: Exception) {
                requestsUiState = RequestsUiState.Error("Failed to fetch requests.")
            }
        }
    }

    fun updateRequestStatus(requestId: String, status: RequestStatus) {
        viewModelScope.launch {
            try {
                val response = HostApi.retrofitService.updateRequestStatus(requestId, UpdateRequestStatusDto(status))
                if (response.isSuccessful) {
                    getRequests()
                } else {
                    requestsUiState = RequestsUiState.Error("Failed to update request.")
                }
            } catch (e: Exception) {
                requestsUiState = RequestsUiState.Error("Failed to update request.")
            }
        }
    }

    fun cancelRequest(requestId: String) {
        viewModelScope.launch {
            try {
                HostApi.retrofitService.cancelRequest(requestId)
                getRequests()
            } catch (e: Exception) {
                requestsUiState = RequestsUiState.Error("Failed to cancel request.")
            }
        }
    }
}

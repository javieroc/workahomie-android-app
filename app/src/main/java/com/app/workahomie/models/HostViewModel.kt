package com.app.workahomie.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.workahomie.data.Host
import com.app.workahomie.network.HostApi
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface HostsUiState {
    data class Success(val hosts: List<Host>) : HostsUiState
    object Error : HostsUiState
    object Loading : HostsUiState
}

class HostViewModel : ViewModel() {
    var hostsUiState: HostsUiState by mutableStateOf(HostsUiState.Loading)
        private set

    init {
        getHosts()
    }

    fun getHosts() {
        viewModelScope.launch {
            hostsUiState = HostsUiState.Loading
            hostsUiState = try {
                val listResult = HostApi.retrofitService.getHosts()
                HostsUiState.Success(listResult.data)
            } catch (e: IOException) {
                HostsUiState.Error
            } catch (e: HttpException) {
                HostsUiState.Error
            }
        }
    }
}
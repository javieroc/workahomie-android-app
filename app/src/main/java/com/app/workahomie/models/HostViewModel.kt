package com.app.workahomie.models

import android.util.Log
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
                Log.d("Pagination", "Fetched ${response.data.size} hosts at offset $offset")
                val newHosts = response.data

                if (newHosts.isEmpty()) {
                    isLastPage = true
                } else {
                    loadedHosts.addAll(newHosts)
                    hostsUiState = HostsUiState.Success(loadedHosts.toList())
                    offset += limit
                }
            } catch (e: IOException) {
                hostsUiState = HostsUiState.Error
            } catch (e: HttpException) {
                hostsUiState = HostsUiState.Error
            } finally {
                isPaginating = false
            }
        }
    }
}
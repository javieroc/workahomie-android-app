package com.app.workahomie.models

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.workahomie.data.Host
import com.app.workahomie.network.HostApi
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface WishlistUiState {
    data class Success(val hosts: List<Host>) : WishlistUiState
    object Error : WishlistUiState
    object Loading : WishlistUiState
}

class WishlistViewModel : ViewModel() {
    private var offset = 0
    private val limit = 10

    var isPaginating by mutableStateOf(false)
        private set

    private var isLastPage = false
    private val loadedHosts = mutableListOf<Host>()

    var wishlistUiState: WishlistUiState by mutableStateOf(WishlistUiState.Loading)
        private set

    init {
        loadMoreHosts()
    }

    fun loadMoreHosts() {
        if (isPaginating || isLastPage) return

        isPaginating = true

        viewModelScope.launch {
            try {
                val response = HostApi.retrofitService.getWishlist(offset, limit)
                val newHosts = response.data

                if (newHosts.isEmpty()) {
                    isLastPage = true
                } else {
                    loadedHosts.addAll(newHosts)
                    offset += limit
                }
                wishlistUiState = WishlistUiState.Success(loadedHosts.toList())
            } catch (e: IOException) {
                wishlistUiState = WishlistUiState.Error
            } catch (e: HttpException) {
                println(e.response())
                Log.e("Error", e.message())
                wishlistUiState = WishlistUiState.Error
            } finally {
                isPaginating = false
            }
        }
    }

    fun refreshWishlist() {
        offset = 0
        isLastPage = false
        loadedHosts.clear()
        wishlistUiState = WishlistUiState.Loading
        loadMoreHosts()
    }
}

package com.app.workahomie.models

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.workahomie.data.WishlistHost
import com.app.workahomie.network.HostApi
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface WishlistUiState {
    data class Success(val hosts: List<WishlistHost>) : WishlistUiState
    object Error : WishlistUiState
    object Loading : WishlistUiState
}

class WishlistViewModel : ViewModel() {
    private var offset = 0
    private val limit = 10

    var isPaginating by mutableStateOf(false)
        private set

    private var isLastPage = false
    private val loadedHosts = mutableListOf<WishlistHost>()

    var wishlistUiState: WishlistUiState by mutableStateOf(WishlistUiState.Loading)
        private set

    init {
        loadMoreWishlistHosts()
    }

    fun loadMoreWishlistHosts() {
        if (isPaginating || isLastPage) return

        isPaginating = true

        viewModelScope.launch {
            try {
                val response = HostApi.retrofitService.getWishlist(offset, limit)
                val newHosts = response.data

                if (newHosts.isEmpty()) {
                    isLastPage = true
                    wishlistUiState = WishlistUiState.Success(loadedHosts.toList())
                } else {
                    loadedHosts.addAll(newHosts)
                    wishlistUiState = WishlistUiState.Success(loadedHosts.toList())
                    offset += limit
                }
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
}

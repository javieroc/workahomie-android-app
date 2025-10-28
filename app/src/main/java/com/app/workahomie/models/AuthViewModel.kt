package com.app.workahomie.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.workahomie.Auth0Client
import com.auth0.android.result.UserProfile
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

enum class AuthState {
    CHECKING,
    LOGGED_IN,
    NOT_LOGGED_IN,
    ERROR
}

sealed class AuthEvent {
    data class NavigateTo(val screen: String) : AuthEvent()
}

class AuthViewModel(
    private val auth0Client: Auth0Client
) : ViewModel() {

    var authState by mutableStateOf(AuthState.CHECKING)
        private set

    var accessToken by mutableStateOf<String?>(null)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var userProfile by mutableStateOf<UserProfile?>(null)
        private set

    private val _events = MutableSharedFlow<AuthEvent>(replay = 1)
    val events = _events.asSharedFlow()

    fun sendDeepLink(screen: String) {
        viewModelScope.launch {
            _events.emit(AuthEvent.NavigateTo(screen))
        }
    }

    init {
        restoreSession()
    }

    fun login() {
        authState = AuthState.CHECKING
        auth0Client.login(
            onSuccess = { token, profile ->
                accessToken = token
                userProfile = profile
                errorMessage = null
                authState = AuthState.LOGGED_IN
            },
            onFailure = {
                errorMessage = it
                authState = AuthState.ERROR
            }
        )
    }

    fun logout() {
        auth0Client.logout(
            onSuccess = {
                accessToken = null
                userProfile = null
                errorMessage = null
                authState = AuthState.NOT_LOGGED_IN
            },
            onFailure = {
                errorMessage = it
                authState = AuthState.ERROR
            }
        )
    }

    fun restoreSession() {
        authState = AuthState.CHECKING
        auth0Client.getSavedCredentials(
            onSuccess = { token, profile ->
                accessToken = token
                userProfile = profile
                errorMessage = null
                authState = AuthState.LOGGED_IN
            },
            onFailure = {
                errorMessage = null
                authState = AuthState.NOT_LOGGED_IN
            }
        )
    }

}

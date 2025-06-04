package com.app.workahomie.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.app.workahomie.Auth0Client
import com.auth0.android.result.UserProfile

enum class AuthState {
    CHECKING,
    LOGGED_IN,
    NOT_LOGGED_IN,
    ERROR
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

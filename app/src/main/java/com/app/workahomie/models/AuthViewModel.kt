package com.app.workahomie.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.app.workahomie.Auth0Client
import com.auth0.android.result.UserProfile

class AuthViewModel(
    private val auth0Client: Auth0Client
) : ViewModel() {

    var isLoggedIn by mutableStateOf(false)
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
        auth0Client.login(
            onSuccess = { token, profile ->
                isLoggedIn = true
                accessToken = token
                userProfile = profile
                errorMessage = null
            },
            onFailure = {
                errorMessage = it
            }
        )
    }

    fun logout() {
        auth0Client.logout(
            onSuccess = {
                isLoggedIn = false
                accessToken = null
                userProfile = null
                errorMessage = null
            },
            onFailure = {
                errorMessage = it
            }
        )
    }

    fun restoreSession() {
        isLoggedIn = false
        accessToken = null
        userProfile = null
        errorMessage = null

        auth0Client.getSavedCredentials(
            onSuccess = { token, profile ->
                isLoggedIn = true
                accessToken = token
                userProfile = profile
            },
            onFailure = {}
        )
    }

}

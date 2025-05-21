package com.app.workahomie.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.workahomie.Auth0Client

class AuthViewModelFactory(
    private val auth0Client: Auth0Client
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(auth0Client) as T
    }
}

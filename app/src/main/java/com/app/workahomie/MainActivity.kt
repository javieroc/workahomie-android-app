package com.app.workahomie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.app.workahomie.models.AuthViewModel
import com.app.workahomie.models.AuthViewModelFactory
import com.app.workahomie.ui.screens.AuthScreen
import com.app.workahomie.ui.theme.WorkahomieTheme
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val domain = getString(R.string.com_auth0_domain)
        val scheme = getString(R.string.com_auth0_scheme)
        val clientId = BuildConfig.AUTH0_CLIENT_ID

        val auth0Client = Auth0Client(this, domain, clientId, scheme)
        val authViewModel = ViewModelProvider(this, AuthViewModelFactory(auth0Client))[AuthViewModel::class.java]


        val apiKey = BuildConfig.MAPS_API_KEY
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        enableEdgeToEdge()
        setContent {
            WorkahomieTheme {
                AuthScreen(authViewModel)
            }
        }
    }
}

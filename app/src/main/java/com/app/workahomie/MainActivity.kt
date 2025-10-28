package com.app.workahomie

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.app.workahomie.models.AuthViewModel
import com.app.workahomie.models.AuthViewModelFactory
import com.app.workahomie.ui.screens.AuthScreen
import com.app.workahomie.ui.theme.WorkahomieTheme
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

        val domain = getString(R.string.com_auth0_domain)
        val scheme = getString(R.string.com_auth0_scheme)
        val clientId = BuildConfig.AUTH0_CLIENT_ID

        val auth0Client = Auth0Client(this, domain, clientId, scheme)
        authViewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(auth0Client)
        )[AuthViewModel::class.java]

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

        // Handle intent if app launched with notification
        intent.getStringExtra("screen")?.let {
            authViewModel.sendDeepLink(it)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val screen = intent.getStringExtra("screen")
        screen?.let { authViewModel.sendDeepLink(it) }
    }
}

package com.app.workahomie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.app.workahomie.screens.AuthScreen
import com.app.workahomie.ui.theme.WorkahomieTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val domain = getString(R.string.com_auth0_domain)
        val scheme = getString(R.string.com_auth0_scheme)
        val clientId = "d50E057uTWRu1R5sbnEauIqUubBP1kCf"

        val auth0Client = Auth0Client(this, domain, clientId, scheme)
        enableEdgeToEdge()
        setContent {
            WorkahomieTheme {
                AuthScreen(auth0Client)
            }
        }
    }
}

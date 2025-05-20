package com.app.workahomie.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.workahomie.Auth0Client

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(auth0Client: Auth0Client) {
    var isLoggedIn by remember { mutableStateOf(false) }
    var accessToken by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            auth0Client.login(
                onSuccess = {
                    isLoggedIn = true
                    accessToken = it.accessToken.orEmpty()
                    errorMessage = null
                },
                onFailure = {
                    errorMessage = it.getDescription()
                }
            )
        }
    }

    if (isLoggedIn) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Welcome") },
                    actions = {
                        IconButton(onClick = {
                            auth0Client.logout(
                                onSuccess = {
                                    isLoggedIn = false
                                    accessToken = ""
                                    errorMessage = null
                                },
                                onFailure = {
                                    errorMessage = it.getDescription()
                                }
                            )
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("You're logged in!")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Access Token:\n$accessToken", fontSize = 12.sp)
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (errorMessage != null) {
                Text("Login Error: $errorMessage", color = Color.Red)
            } else {
                CircularProgressIndicator()
            }
        }
    }
}

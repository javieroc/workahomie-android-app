package com.app.workahomie.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.app.workahomie.models.AuthViewModel

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val isLoggedIn = viewModel.isLoggedIn
    val error = viewModel.errorMessage

    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            viewModel.login()
        }
    }

    if (isLoggedIn) {
        ProfileScreen(viewModel)
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (error != null) {
                Text("Login Error: $error", color = Color.Red)
            } else {
                CircularProgressIndicator()
            }
        }
    }
}

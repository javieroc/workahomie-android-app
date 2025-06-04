package com.app.workahomie.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.app.workahomie.models.AuthViewModel

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val isLoggedIn = viewModel.isLoggedIn
    val error = viewModel.errorMessage
    val user = viewModel.userProfile

    LaunchedEffect(key1 = isLoggedIn, key2 = error) {
        if (!isLoggedIn && error == null) {
            viewModel.login()
        }
    }

    when {
        isLoggedIn && user != null -> {
            MainScreen(authViewModel = viewModel)
        }
        error != null -> {
            ErrorScreen(error)
        }
        else -> {
            LoadingScreen()
        }
    }
}

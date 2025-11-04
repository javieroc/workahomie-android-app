package com.app.workahomie.ui.screens

import androidx.compose.runtime.Composable
import com.app.workahomie.models.AuthState
import com.app.workahomie.models.AuthViewModel

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val state = viewModel.authState
    val error = viewModel.errorMessage

    when (state) {
        AuthState.CHECKING -> LoadingScreen()

        AuthState.LOGGED_IN -> MainScreen(authViewModel = viewModel)

        AuthState.NOT_LOGGED_IN -> WelcomeScreen(onLoginClick = { viewModel.login() })

        AuthState.ERROR -> ErrorScreen(error ?: "Unknown error")
    }
}
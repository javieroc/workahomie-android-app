package com.app.workahomie.ui.screens

import androidx.compose.runtime.Composable
import com.app.workahomie.models.AuthViewModel

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val isLoggedIn = viewModel.isLoggedIn
    val error = viewModel.errorMessage

    if (isLoggedIn) {

        MainScreen(
            authViewModel = viewModel,
        )
    } else {
        if (error != null) {
            ErrorScreen(error)
        } else {
            LoadingScreen()
        }
    }
}

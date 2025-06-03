package com.app.workahomie.ui.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.workahomie.models.AuthViewModel
import com.app.workahomie.models.HostViewModel

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val isLoggedIn = viewModel.isLoggedIn
    val error = viewModel.errorMessage

    if (isLoggedIn) {
        val hostViewModel: HostViewModel = viewModel()
        MainScreen(
            authViewModel = viewModel,
            hostViewModel = hostViewModel,
        )
    } else {
        if (error != null) {
            ErrorScreen(error)
        } else {
            LoadingScreen()
        }
    }
}

package com.app.workahomie.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.workahomie.models.AuthViewModel
import com.app.workahomie.models.MarsViewModel

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
        val marsViewModel: MarsViewModel = viewModel()
        HomeScreen(
            marsUiState = marsViewModel.marsUiState,
        )
        // ProfileScreen(viewModel)
    } else {
        if (error != null) {
            ErrorScreen(error)
        } else {
            LoadingScreen()
        }
    }
}

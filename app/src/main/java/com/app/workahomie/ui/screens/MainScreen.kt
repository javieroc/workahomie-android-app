package com.app.workahomie.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.workahomie.models.AuthViewModel
import com.app.workahomie.models.HostViewModel
import com.app.workahomie.ui.components.BottomNavScreen
import com.app.workahomie.ui.components.BottomNavigationBar

@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
) {
    val navController = rememberNavController()
    val hostViewModel: HostViewModel = viewModel()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavScreen.Home.route) {
                HostsScreen(hostViewModel)
            }
            composable(BottomNavScreen.Explore.route) {
                WishlistScreen()
            }
            composable(BottomNavScreen.Profile.route) {
                ProfileScreen(authViewModel)
            }
        }
    }
}

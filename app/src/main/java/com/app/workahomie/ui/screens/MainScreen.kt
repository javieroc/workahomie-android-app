package com.app.workahomie.ui.screens

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.workahomie.data.Host
import com.app.workahomie.models.AuthEvent
import com.app.workahomie.models.AuthViewModel
import com.app.workahomie.models.HostViewModel
import com.app.workahomie.ui.components.BottomNavScreen
import com.app.workahomie.ui.components.BottomNavigationBar
import com.google.gson.Gson

@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
) {
    val navController = rememberNavController()
    val hostViewModel: HostViewModel = viewModel()

    LaunchedEffect(Unit) {
        authViewModel.events.collect { event ->
            when (event) {
                is AuthEvent.NavigateTo -> navController.navigate(BottomNavScreen.Requests.route) {
                    popUpTo(BottomNavScreen.Home.route) { inclusive = false }
                }
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None }
        ) {
            composable(BottomNavScreen.Home.route) {
                HostsScreen(navController = navController)
            }
            composable(
                route = "hostDetails/{hostJson}",
                arguments = listOf(navArgument("hostJson") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val hostJson = backStackEntry.arguments?.getString("hostJson")
                val host = Gson().fromJson(hostJson, Host::class.java)
                HostDetailsScreen(
                    host = host,
                    navController = navController,
                    authViewModel = authViewModel,
                    hostViewModel = hostViewModel
                )
            }
            composable(BottomNavScreen.Explore.route) {
                WishlistScreen(navController = navController)
            }
            composable(BottomNavScreen.Requests.route) {
                RequestsScreen()
            }
            composable(BottomNavScreen.Profile.route) {
                ProfileScreen(hostViewModel)
            }
        }
    }
}

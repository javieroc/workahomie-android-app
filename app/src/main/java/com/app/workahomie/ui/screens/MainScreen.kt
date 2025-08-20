package com.app.workahomie.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.workahomie.data.Host
import com.app.workahomie.models.AuthViewModel
import com.app.workahomie.ui.components.BottomNavScreen
import com.app.workahomie.ui.components.BottomNavigationBar
import com.google.gson.Gson

@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
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
                HostDetailsScreen(host = host, navController = navController, authViewModel = authViewModel)
            }
            composable(BottomNavScreen.Explore.route) {
                WishlistScreen(navController = navController)
            }
            composable(BottomNavScreen.Profile.route) {
                ProfileScreen(authViewModel)
            }
        }
    }
}

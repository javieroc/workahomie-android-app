package com.app.workahomie.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavScreen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : BottomNavScreen("hosts", "Hosts", Icons.Default.Home)
    data object Explore : BottomNavScreen("wishlist", "Wishlist", Icons.Default.FavoriteBorder)
    data object Requests : BottomNavScreen("requests", "My Requests", Icons.AutoMirrored.Filled.Send)
    data object Settings : BottomNavScreen("settings", "Settings", Icons.Default.Settings)

    companion object {
        val items = listOf(Home, Explore, Requests, Settings)
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val textColor = MaterialTheme.colorScheme.onSurface
    val indicatorColor = MaterialTheme.colorScheme.primary

    NavigationBar {
        BottomNavScreen.items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(screen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = textColor,
                    selectedTextColor = textColor,
                    unselectedTextColor = textColor,
                    indicatorColor = indicatorColor
                )
            )
        }
    }
}


package com.app.workahomie.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavScreen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : BottomNavScreen("hosts", "Hosts", Icons.Default.Home)
    data object Explore : BottomNavScreen("wishlist", "Wishlist", Icons.Default.FavoriteBorder)
    data object Requests : BottomNavScreen("requests", "My Requests", Icons.Default.Send)
    data object Profile : BottomNavScreen("profile", "Profile", Icons.Default.Person)

    companion object {
        val items = listOf(Home, Explore, Requests, Profile)
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.White else Color.Black
    val indicatorColor = if (isDarkTheme) Color(0xFF805AD5) else Color(0xFFD53F8C)

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


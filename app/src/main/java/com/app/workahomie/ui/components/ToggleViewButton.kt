package com.app.workahomie.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun ToggleViewButton(
    isMapView: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.material3.FloatingActionButton(
        onClick = onClick
    ) {
        val icon = if (isMapView) Icons.AutoMirrored.Filled.List else Icons.Default.Place
        Icon(imageVector = icon, contentDescription = null)
    }
}

package com.app.workahomie.constants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Shower
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.vector.ImageVector

val facilityIcons: Map<String, ImageVector> = mapOf(
    "wifi" to Icons.Default.Wifi,
    "parking" to Icons.Default.LocalParking,
    "coffee" to Icons.Default.Coffee,
    "kitchen" to Icons.Default.Kitchen,
    "showers" to Icons.Default.Shower,
    "garden" to Icons.Default.Grass,
    "snacks" to Icons.Default.Fastfood
)

val FACILITIES: List<String> = facilityIcons.map { it -> it.key }

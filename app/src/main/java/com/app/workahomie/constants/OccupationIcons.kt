package com.app.workahomie.constants

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

data class OccupationItem(
    val name: String,
    val icon: ImageVector
)

val occupationIcons: Map<String, ImageVector> = mapOf(
    "Accountant" to Icons.Default.AccountBalance,
    "Architect" to Icons.Default.Architecture,
    "Artist" to Icons.Default.Palette,
    "Business Analyst" to Icons.Default.PieChart,
    "Consultant" to Icons.Default.Business,
    "Data Scientist" to Icons.Default.DataObject,
    "Doctor" to Icons.Default.LocalHospital,
    "Engineer" to Icons.Default.Code,
    "Environmental Scientist" to Icons.Default.Eco,
    "Filmmaker" to Icons.Default.Movie,
    "Graphic Designer" to Icons.Default.Brush,
    "Illustrator" to Icons.Default.Draw,
    "Lawyer" to Icons.Default.Gavel,
    "Marketing Specialist" to Icons.Default.Sell,
    "Musician" to Icons.Default.MusicNote,
    "Photographer" to Icons.Default.PhotoCamera,
    "Product Manager" to Icons.Default.ManageAccounts,
    "Project Manager" to Icons.AutoMirrored.Filled.ReceiptLong,
    "Sales Representative" to Icons.Default.Sell,
    "Scientist" to Icons.Default.Science,
    "Software Developer" to Icons.Default.Code,
    "Teacher/Tutor" to Icons.Default.School,
    "UI/UX Designer" to Icons.Default.DesignServices,
    "Videographer" to Icons.Default.Videocam,
    "Virtual Assistant" to Icons.Default.SupportAgent,
    "Writer" to Icons.Default.Create,
    "Others" to Icons.Default.Work
)

val OCCUPATIONS: List<OccupationItem> = occupationIcons.map { (label, icon) ->
    OccupationItem(
        name = label.lowercase(),
        icon = icon
    )
}

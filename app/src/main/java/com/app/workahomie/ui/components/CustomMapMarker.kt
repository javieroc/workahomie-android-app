package com.app.workahomie.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.workahomie.constants.occupationIcons
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState

fun markerShape() = GenericShape { size, _ ->
    val width = size.width
    val height = size.height
    val pointerHeight = 20f
    val cornerRadius = 30f

    // Rounded rectangle body
    moveTo(cornerRadius, 0f)
    lineTo(width - cornerRadius, 0f)
    quadraticTo(width, 0f, width, cornerRadius)
    lineTo(width, height - pointerHeight - cornerRadius)
    quadraticTo(width, height - pointerHeight, width - cornerRadius, height - pointerHeight)
    lineTo(cornerRadius, height - pointerHeight)
    quadraticTo(0f, height - pointerHeight, 0f, height - pointerHeight - cornerRadius)
    lineTo(0f, cornerRadius)
    quadraticTo(0f, 0f, cornerRadius, 0f)

    // Triangle pointer
    moveTo(width / 2 - 20f, height - pointerHeight)
    lineTo(width / 2f, height)
    lineTo(width / 2 + 20f, height - pointerHeight)
    close()
}

@Composable
fun CustomMapMarker(
    occupation: String,
    location: LatLng,
    onClick: () -> Unit
) {
    val markerState = remember { MarkerState(position = location) }

    MarkerComposable(
        keys = arrayOf(occupation),
        state = markerState,
        title = occupation,
        anchor = Offset(0.5f, 1f),
        onClick = {
            onClick()
            true
        }
    ) {
        Row(
            modifier = Modifier
                .background(Color(0xFF805AD5), shape = markerShape()) // Purple background
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            occupationIcons[occupation]?.let {
                Icon(
                    imageVector = it,
                    contentDescription = occupation,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White // White icon
                )
            }
            Text(
                text = occupation,
                color = Color.White, // White text
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

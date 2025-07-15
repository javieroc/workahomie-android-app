package com.app.workahomie.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
                .background(Color.White, RoundedCornerShape(8.dp, 8.dp, 8.dp, 0.dp))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            occupationIcons[occupation]?.let {
                Icon(
                    imageVector = it,
                    contentDescription = occupation,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF805AD5)
                )
            }
            Text(text = occupation)
        }
    }
}

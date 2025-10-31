package com.app.workahomie.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.app.workahomie.data.Request
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import com.app.workahomie.data.RequestStatus

@Composable
fun RequestCard(
    request: Request,
    isIncoming: Boolean,
    onAccept: () -> Unit = {},
    onReject: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Avatar + Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = if (isIncoming) request.userAvatar else request.host.profileImages.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = if (isIncoming) {
                            "${request.userName ?: "Unknown"} requested to stay"
                        } else {
                            "You requested to stay with ${request.host.firstName}"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "From ${formatDate(request.checkIn)} to ${formatDate(request.checkOut)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // WhatsApp / Phone number of the host
            if (!isIncoming && !request.host.phone.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "WhatsApp",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF805AD5)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = request.host.phone,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Status
            Text(
                text = "Status: ${request.status}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .background(
                        color = when (request.status) {
                            RequestStatus.pending -> Color(0xfff2b035).copy(alpha = 0.2f)
                            RequestStatus.accepted -> Color(0xff0cf25d).copy(alpha = 0.2f)
                            RequestStatus.declined -> Color(0xffd92525).copy(alpha = 0.2f)
                            else -> Color.LightGray.copy(alpha = 0.2f)
                        },
                        shape = CircleShape
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Actions
            if (isIncoming && request.status == RequestStatus.pending) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF805AD5))
                    ) {
                        Text(
                            text = "Accept",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OutlinedButton(
                        onClick = onReject,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF805AD5)),
                        border = BorderStroke(1.dp, Color(0xFF805AD5))
                    ) {
                        Text("Reject")
                    }
                }
            } else if (!isIncoming && request.status == RequestStatus.pending) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF805AD5)),
                    border = BorderStroke(1.dp, Color(0xFF805AD5))
                ) {
                    Text("Cancel Request")
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val offsetDateTime = OffsetDateTime.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        formatter.format(offsetDateTime)
    } catch (e: Exception) {
        dateString
    }
}

package com.app.workahomie.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.app.workahomie.data.Host

@Composable
fun HostMiniCard(
    host: Host,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        // Close button top-right
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = Color.Gray,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(20.dp)
                .clickable { onClose() }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Profile picture
            AsyncImage(
                model = host.profileImages.firstOrNull(),
                contentDescription = "Host profile picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${host.firstName} ${host.lastName}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                Text(
                    text = host.occupation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = host.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }

            // Phone action
            host.phone?.let { phoneNumber ->
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call host",
                    tint = Color(0xFF805AD5),
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://wa.me/$phoneNumber")
                            }
                            context.startActivity(intent)
                        }
                )
            }
        }
    }
}

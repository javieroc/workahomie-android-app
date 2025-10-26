package com.app.workahomie.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.app.workahomie.data.Host

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostBottomSheet(
    host: Host,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onHostClick: (Host) -> Unit
) {
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Drag handle imitation
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Gray.copy(alpha = 0.4f))
                    .align(Alignment.CenterHorizontally)
            )

            // Profile row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = host.profileImages.firstOrNull(),
                    contentDescription = "Host avatar",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                )
                Column(modifier = Modifier.clickable { onHostClick(host) }) {
                    Text(
                        text = "${host.firstName} ${host.lastName}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = host.occupation, style = MaterialTheme.typography.bodyMedium)

                    host.phone?.let { phoneNumber ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://wa.me/$phoneNumber")
                                }
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "WhatsApp",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = phoneNumber)
                        }
                    }
                }
            }

            HorizontalDivider()

            // Workspace details
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Workspace", style = MaterialTheme.typography.titleSmall)
                Text(text = host.address, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = host.placeDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // Rating row (static for now)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = Color(0xFFFFC107)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("4.3 (10 reviews)", style = MaterialTheme.typography.bodyMedium)
            }

            // Preview picture (first image)
            host.pictures.firstOrNull()?.let { picture ->
                AsyncImage(
                    model = picture,
                    contentDescription = "Workspace preview",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

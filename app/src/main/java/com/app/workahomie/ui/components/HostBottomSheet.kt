package com.app.workahomie.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
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
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Profile image
            AsyncImage(
                model = host.profileImages.firstOrNull(),
                contentDescription = "Host profile picture",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Name & occupation
            Text("${host.firstName} ${host.lastName}", style = MaterialTheme.typography.titleMedium)
            Text(host.occupation, style = MaterialTheme.typography.bodyMedium)
            Text(host.address, style = MaterialTheme.typography.bodySmall, color = Color.Gray)

            // Phone (WhatsApp link)
            host.phone?.let { phoneNumber ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://wa.me/$phoneNumber")
                            }
                            context.startActivity(intent)
                        }
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color(0xFF805AD5))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(phoneNumber)
                }
            }
        }
    }
}

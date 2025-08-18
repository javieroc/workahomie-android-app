package com.app.workahomie.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.app.workahomie.data.Host
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostDetailsScreen(
    host: Host,
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val images = host.pictures
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { images.size })
    val context = LocalContext.current

    val dateRangePickerState = rememberDateRangePickerState()
    var message by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    fun Long?.toDateString(): String {
        if (this == null) return ""
        val instant = Instant.ofEpochMilli(this)
        return instant.atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    val checkInDate = dateRangePickerState.selectedStartDateMillis.toDateString()
    val checkOutDate = dateRangePickerState.selectedEndDateMillis.toDateString()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DateRangePicker(state = dateRangePickerState)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Box {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) { page ->
                    AsyncImage(
                        model = images[page],
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(8.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    repeat(images.size) { index ->
                        val selected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .size(if (selected) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (selected) Color.White else Color.Gray)
                        )
                    }
                }

                Text(
                    text = "${pagerState.currentPage + 1} / ${images.size}",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        item {
            Column {
                Text(
                    text = host.address,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = host.placeDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xffd53f8c))
                Text(
                    text = "4.3 (10) reviews",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        item { HorizontalDivider() }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = host.profileImages[0],
                    contentDescription = "Host avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Column {
                    Text(
                        text = "${host.firstName} ${host.lastName}",
                    )
                    Text(text = host.occupation)
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
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = phoneNumber)
                        }
                    }
                }
            }
        }

        item { HorizontalDivider() }

        item {
            Text(
                text = "Request to stay",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDatePicker = true }
                ) {
                    OutlinedTextField(
                        value = checkInDate,
                        onValueChange = {},
                        label = { Text("Check-in") },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showDatePicker = true }
                ) {
                    OutlinedTextField(
                        value = checkOutDate,
                        onValueChange = {},
                        label = { Text("Check-out") },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message (optional)") },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item {
            Button(
                onClick = {
                    // TODO: Handle submission, e.g., call a ViewModel method
                    println("Booking submitted: $checkInDate, $checkOutDate, $message")
                },
                enabled = checkInDate.isNotEmpty() && checkOutDate.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Request")
            }
        }
    }
}

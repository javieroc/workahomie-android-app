package com.app.workahomie.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestToStayForm(
    onSubmit: (checkIn: String, checkOut: String, message: String) -> Unit
) {
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

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HorizontalDivider()

        Text(
            text = "Request to stay",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 8.dp)
        )

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

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message (optional)") },
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = {
                onSubmit(checkInDate, checkOutDate, message)
            },
            enabled = checkInDate.isNotEmpty() && checkOutDate.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Request")
        }
    }
}

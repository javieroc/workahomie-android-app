package com.app.workahomie.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.workahomie.utils.allCountries

@Composable
fun PhoneInputField(
    initialDialCode: String = "+1",
    initialNumber: String = "",
    onPhoneChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCountry by remember(initialDialCode) {
        mutableStateOf(allCountries.find { it.dialCode == initialDialCode } ?: allCountries.first())
    }
    var phoneNumber by remember { mutableStateOf(initialNumber) }

    fun fullPhone() = "${selectedCountry.dialCode}$phoneNumber"

    LaunchedEffect(selectedCountry, phoneNumber) {
        onPhoneChange(fullPhone())
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box {
            OutlinedButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.width(120.dp)
            ) {
                Text(
                    text = "${selectedCountry.flagEmoji} ${selectedCountry.dialCode}"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.width(300.dp)
            ) {
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .heightIn(max = 400.dp)
                        .verticalScroll(scrollState)
                ) {
                    allCountries.forEach { country ->
                        DropdownMenuItem(
                            text = { Text("${country.flagEmoji} ${country.name} (${country.dialCode})") },
                            onClick = {
                                selectedCountry = country
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it.filter(Char::isDigit) },
            label = { Text("Phone number") },
            modifier = Modifier.weight(1f)
        )
    }
}

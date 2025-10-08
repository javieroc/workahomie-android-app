package com.app.workahomie.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.app.workahomie.data.Host
import com.app.workahomie.models.AuthViewModel

@Composable
fun HostProfileForm(
    authViewModel: AuthViewModel,
    host: Host,
    onSaveProfile: (Host) -> Unit,
    onSavePlace: (Host) -> Unit
) {
    val scrollState = rememberScrollState()
    val profile = authViewModel.userProfile

    // --- Host personal information ---
    var firstName by remember { mutableStateOf(host.firstName) }
    var lastName by remember { mutableStateOf(host.lastName) }
    var occupation by remember { mutableStateOf(host.occupation) }
    var aboutMe by remember { mutableStateOf(host.aboutMe) }
    var phone by remember { mutableStateOf(host.phone ?: "") }

    // --- Workspace info ---
    var address by remember { mutableStateOf(host.address) }
    var placeDescription by remember { mutableStateOf(host.placeDescription) }
    var placeDetails by remember { mutableStateOf(host.placeDetails) }
    var facilities by remember { mutableStateOf(host.facilities) }

    // --- Images ---
    val imageUris = remember { mutableStateListOf<Uri>() }
    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            imageUris.addAll(uris)
        }

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- Section: Profile ---
        Text("Host Profile", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = occupation,
            onValueChange = { occupation = it },
            label = { Text("Occupation") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = aboutMe,
            onValueChange = { aboutMe = it },
            label = { Text("About Me") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone (WhatsApp)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onSaveProfile(
                    host.copy(
                        firstName = firstName,
                        lastName = lastName,
                        occupation = occupation,
                        aboutMe = aboutMe,
                        phone = phone
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Profile")
        }

        HorizontalDivider()

        // --- Section: Workspace ---
        Text("Workspace", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = placeDescription,
            onValueChange = { placeDescription = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = placeDetails,
            onValueChange = { placeDetails = it },
            label = { Text("Details") },
            modifier = Modifier.fillMaxWidth()
        )

        // --- Facilities ---
        FacilitySelector(facilities = facilities) { facilities = it }

        // --- Pictures ---
        Text("Pictures", style = MaterialTheme.typography.titleMedium)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(imageUris.size) { index ->
                AsyncImage(
                    model = imageUris[index],
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            item {
                IconButton(onClick = { imagePicker.launch("image/*") }) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Add photo")
                }
            }
        }

        Button(
            onClick = {
                onSavePlace(
                    host.copy(
                        address = address,
                        placeDescription = placeDescription,
                        placeDetails = placeDetails,
                        facilities = facilities,
                        pictures = imageUris.map { it.toString() }
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Place")
        }
    }
}

@Composable
fun FacilitySelector(
    facilities: List<String>,
    onFacilitiesChange: (List<String>) -> Unit
) {
    val options = listOf("garden", "showers", "parking", "coffee", "kitchen", "wifi", "snacks")

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        options.forEach { option ->
            val selected = option in facilities
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = selected,
                    onCheckedChange = {
                        onFacilitiesChange(
                            if (selected) facilities - option else facilities + option
                        )
                    }
                )
                Text(option.replaceFirstChar { it.uppercase() })
            }
        }
    }
}


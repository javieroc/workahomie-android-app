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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.app.workahomie.data.Host

@Composable
fun HostProfileForm(
    host: Host,
    onSaveProfile: (Host, Uri?) -> Unit,
    onSavePlace: (Host) -> Unit
) {
    val scrollState = rememberScrollState()

    // --- Personal info ---
    var firstName by remember { mutableStateOf(host.firstName) }
    var lastName by remember { mutableStateOf(host.lastName) }
    var occupation by remember { mutableStateOf(host.occupation) }
    var aboutMe by remember { mutableStateOf(host.aboutMe) }
    var phone by remember { mutableStateOf(host.phone ?: "") }

    // --- Profile picture (single) ---
    var profileUri by remember { mutableStateOf<Uri?>(null) }
    val profilePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        profileUri = uri
    }

    // --- Workspace info ---
    var address by remember { mutableStateOf(host.address) }
    var placeDescription by remember { mutableStateOf(host.placeDescription) }
    var placeDetails by remember { mutableStateOf(host.placeDetails) }
    var facilities by remember { mutableStateOf(host.facilities) }

    // --- Workspace pictures (multiple) ---
    val pictureUris = remember { mutableStateListOf<Uri>() }
    val picturePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        pictureUris.addAll(uris)
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

        // --- Profile picture ---
        Text("Profile Picture", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            val profileImage = profileUri ?: host.profileImages.firstOrNull()?.let { Uri.parse(it) }
            profileImage?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Profile picture",
                    modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            IconButton(onClick = { profilePicker.launch("image/*") }) {
                Icon(Icons.Default.AddAPhoto, contentDescription = "Pick profile picture")
            }
        }

        Button(
            onClick = {
                onSaveProfile(
                    host.copy(
                        firstName = firstName,
                        lastName = lastName,
                        occupation = occupation,
                        aboutMe = aboutMe,
                        phone = phone,
                    ),
                    profileUri
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

        FacilitySelector(facilities = facilities) { facilities = it }

        // --- Workspace pictures ---
        Text("Workspace Pictures", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Existing workspace pictures
            host.pictures.forEach { picture ->
                item {
                    AsyncImage(
                        model = picture,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            // New pictures selected by the user
            items(pictureUris.size) { index ->
                AsyncImage(
                    model = pictureUris[index],
                    contentDescription = null,
                    modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            // Add picture button
            item {
                IconButton(onClick = { picturePicker.launch("image/*") }) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Add workspace photo")
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
                        pictures = host.pictures + pictureUris.map { it.toString() }
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

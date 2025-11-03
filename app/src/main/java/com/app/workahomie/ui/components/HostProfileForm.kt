package com.app.workahomie.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil3.compose.AsyncImage
import com.app.workahomie.data.Host
import com.app.workahomie.utils.splitPhoneNumber

@Composable
fun HostProfileForm(
    host: Host,
    onSaveProfile: (Host, Uri?) -> Unit
) {
    var firstName by remember { mutableStateOf(host.firstName) }
    var lastName by remember { mutableStateOf(host.lastName) }
    var occupation by remember { mutableStateOf(host.occupation) }
    var aboutMe by remember { mutableStateOf(host.aboutMe) }
    val (initialDialCode, initialNumber) = splitPhoneNumber(host.phone ?: "")
    var phone by remember { mutableStateOf("${initialDialCode}${initialNumber}") }

    var profileUri by remember { mutableStateOf<Uri?>(null) }
    val profilePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        profileUri = it
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Host Profile", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(firstName, { firstName = it }, label = { Text("First Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(lastName, { lastName = it }, label = { Text("Last Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(occupation, { occupation = it }, label = { Text("Occupation") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(aboutMe, { aboutMe = it }, label = { Text("About Me") }, modifier = Modifier.fillMaxWidth())
        PhoneInputField(
            initialDialCode = initialDialCode,
            initialNumber = initialNumber,
            onPhoneChange = { newValue -> phone = newValue }
        )

        Text("Profile Picture", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            val profileImage = profileUri ?: host.profileImages.firstOrNull()?.let { Uri.parse(it) }
            profileImage?.let {
                AsyncImage(model = it, contentDescription = "Profile", modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
            }
            IconButton(onClick = { profilePicker.launch("image/*") }) {
                Icon(Icons.Default.AddAPhoto, contentDescription = "Pick profile picture")
            }
        }

        Button(
            onClick = {
                val updatedHost = host.copy(
                    firstName = firstName,
                    lastName = lastName,
                    occupation = occupation,
                    aboutMe = aboutMe,
                    phone = phone
                )
                onSaveProfile(updatedHost, profileUri)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Save Profile",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

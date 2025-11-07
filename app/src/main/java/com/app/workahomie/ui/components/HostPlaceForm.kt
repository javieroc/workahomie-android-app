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
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import coil3.compose.AsyncImage
import com.app.workahomie.data.Host
import com.app.workahomie.utils.parseAddress

@Composable
fun HostPlaceForm(
    host: Host,
    onSavePlace: (Host, List<Uri>) -> Unit
) {
    val parsedAddress = parseAddress(host.address)
    var addressJson by remember { mutableStateOf(parsedAddress.rawJson) }
    var addressDisplay by remember { mutableStateOf(parsedAddress.displayName) }

    var placeDescription by remember { mutableStateOf(host.placeDescription) }
    var placeDetails by remember { mutableStateOf(host.placeDetails) }
    var facilities by remember { mutableStateOf(host.facilities) }

    val pictureUris = remember { mutableStateListOf<Uri>() }
    val picturePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris -> pictureUris.addAll(uris) }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Workspace", style = MaterialTheme.typography.titleLarge)

        AddressInputField(
            initialAddress = addressDisplay,
            onAddressSelected = { json ->
                addressJson = json
                addressDisplay = parseAddress(json).displayName
            }
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

        Text("Workspace Pictures", style = MaterialTheme.typography.titleMedium)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp), // optional max height for scroll inside the screen
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Show existing pictures
            items(host.pictures) { pic ->
                AsyncImage(
                    model = pic,
                    contentDescription = null,
                    modifier = Modifier
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }

            // Show newly picked pictures
            items(pictureUris) { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }

            // Add photo button as the last item
            item {
                IconButton(
                    onClick = { picturePicker.launch("image/*") },
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Add workspace photo")
                }
            }
        }

        Button(
            onClick = {
                val updatedHost = host.copy(
                    address = addressJson,
                    placeDescription = placeDescription,
                    placeDetails = placeDetails,
                    facilities = facilities,
                )
                onSavePlace(updatedHost, pictureUris)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Save Workspace",
                fontWeight = FontWeight.Bold
            )
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

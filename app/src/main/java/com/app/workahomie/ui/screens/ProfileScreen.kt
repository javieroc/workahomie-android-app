package com.app.workahomie.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.workahomie.models.AuthViewModel
import com.app.workahomie.models.HostViewModel
import com.app.workahomie.models.HostDetailsUiState
import com.app.workahomie.ui.components.HostPlaceForm
import com.app.workahomie.ui.components.HostProfileForm

@Composable
fun ProfileScreen(
    hostViewModel: HostViewModel,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val hostState by hostViewModel.hostState

    // Fetch the current host data when the screen is first shown
    LaunchedEffect(Unit) {
        hostViewModel.fetchHostMe()
    }

    when (val state = hostState) {
        is HostDetailsUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is HostDetailsUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        is HostDetailsUiState.Success -> {
            val host = state.host
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Host Settings",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                // Subtitle for first-time host
                if (host.id.isEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Welcome! Fill out the form below to become a host and share your workspace.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                HostProfileForm(
                    host,
                    onSaveProfile = { updatedHost, profileUri ->
                        hostViewModel.saveHost(updatedHost, profileUri, context)
                        val message = if (host.id.isEmpty()) {
                            "You’re now a host! Profile created successfully"
                        } else {
                            "Profile updated successfully"
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                HostPlaceForm(
                    host,
                    onSavePlace = { updatedHost, pictureUris ->
                        hostViewModel.updateHostPlace(updatedHost, pictureUris, context)
                        Toast.makeText(context, "Workspace updated successfully", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { authViewModel.logout() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text(
                        text = "Logout",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        HostDetailsUiState.Idle -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading profile…")
            }
        }
    }
}

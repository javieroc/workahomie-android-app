package com.app.workahomie.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.app.workahomie.data.Request
import com.app.workahomie.models.RequestViewModel
import com.app.workahomie.models.RequestsUiState
import com.app.workahomie.ui.components.EmptyState

@Composable
fun RequestsScreen(requestViewModel: RequestViewModel = viewModel()) {
    val requestsUiState = requestViewModel.requestsUiState

    when (requestsUiState) {
        is RequestsUiState.Loading -> {
            CircularProgressIndicator()
        }
        is RequestsUiState.Error -> {
            Text(text = requestsUiState.message)
        }
        is RequestsUiState.Success -> {
            RequestsContent(requestsUiState.data.incoming, requestsUiState.data.outgoing)
        }
    }
}

@Composable
fun RequestsContent(incoming: List<Request>, outgoing: List<Request>) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Incoming", "Outgoing")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }
        when (selectedTabIndex) {
            0 -> RequestsList(requests = incoming, isIncoming = true)
            1 -> RequestsList(requests = outgoing, isIncoming = false)
        }
    }
}

@Composable
fun RequestsList(requests: List<Request>, isIncoming: Boolean) {
    if (requests.isEmpty()) {
        EmptyState(message = "You have no ${if (isIncoming) "incoming" else "outgoing"} requests.")
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(requests) { request ->
                RequestCard(request = request, isIncoming = isIncoming)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun RequestCard(request: Request, isIncoming: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = if (isIncoming) request.userAvatar else request.host.profileImages.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = if (isIncoming) request.userName ?: "" else request.host.firstName)
                    Text(text = "${request.checkIn} - ${request.checkOut}")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Status: ${request.status}")
            if (isIncoming && request.status == "pending") {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { /* TODO: Implement accept request */ }) {
                        Text("Accept")
                    }
                    Button(onClick = { /* TODO: Implement reject request */ }) {
                        Text("Reject")
                    }
                }
            }
        }
    }
}

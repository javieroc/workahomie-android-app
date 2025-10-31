package com.app.workahomie.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.workahomie.data.Request
import com.app.workahomie.data.RequestStatus
import com.app.workahomie.models.RequestViewModel
import com.app.workahomie.models.RequestsUiState
import com.app.workahomie.ui.components.EmptyState
import com.app.workahomie.ui.components.RequestCard

@Composable
fun RequestsScreen(requestViewModel: RequestViewModel = viewModel()) {
    val requestsUiState = requestViewModel.requestsUiState

    when (requestsUiState) {
        is RequestsUiState.Loading -> {
            LoadingScreen()
        }
        is RequestsUiState.Error -> {
            Text(text = requestsUiState.message)
        }
        is RequestsUiState.Success -> {
            RequestsContent(
                requestsUiState.data.incoming,
                requestsUiState.data.outgoing,
                requestViewModel
            )
        }
    }
}

@Composable
fun RequestsContent(
    incoming: List<Request>,
    outgoing: List<Request>,
    requestViewModel: RequestViewModel
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Outgoing", "Incoming")

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
            0 -> RequestsList(requests = outgoing, isIncoming = false, requestViewModel = requestViewModel)
            1 -> RequestsList(requests = incoming, isIncoming = true, requestViewModel = requestViewModel)
        }
    }
}

@Composable
fun RequestsList(
    requests: List<Request>,
    isIncoming: Boolean,
    requestViewModel: RequestViewModel
) {
    if (requests.isEmpty()) {
        EmptyState(message = "You have no ${if (isIncoming) "incoming" else "outgoing"} requests.")
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(requests) { request ->
                RequestCard(
                    request = request,
                    isIncoming = isIncoming,
                    onAccept = { requestViewModel.updateRequestStatus(request.id, RequestStatus.accepted) },
                    onReject = { requestViewModel.updateRequestStatus(request.id, RequestStatus.declined) },
                    onCancel = { requestViewModel.cancelRequest(request.id) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

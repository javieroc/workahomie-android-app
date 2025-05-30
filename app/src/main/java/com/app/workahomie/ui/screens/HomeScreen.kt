package com.app.workahomie.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.workahomie.data.Host
import com.app.workahomie.models.HostsUiState
import com.app.workahomie.ui.components.HostCard

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    hostsUiState: HostsUiState,
    modifier: Modifier = Modifier,
) {
    Scaffold {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize(),
        ) {
            when (hostsUiState) {
                is HostsUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
                is HostsUiState.Success -> HostsListScreen(
                    hostsUiState.hosts, modifier = modifier.fillMaxWidth()
                )
                is HostsUiState.Error -> ErrorScreen( error = "Failed to load", modifier = modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun HostsListScreen(
    hosts: List<Host>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        items(items = hosts, key = { host -> host.id }) { host ->
            HostCard(
                host = host,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
            )
        }
    }
}
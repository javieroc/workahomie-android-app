package com.app.workahomie.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.workahomie.data.Host
import com.app.workahomie.models.HostViewModel
import com.app.workahomie.models.HostsUiState
import com.app.workahomie.ui.components.HostCard
import com.app.workahomie.ui.components.ToggleViewButton

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HostsScreen(
    viewModel: HostViewModel = viewModel(),
    modifier: Modifier = Modifier,
) {
    val hostsUiState = viewModel.hostsUiState
    val isPaginating = viewModel.isPaginating
    val isMapView = viewModel.isMapView

    Scaffold(
        floatingActionButton = {
            ToggleViewButton(
                isMapView = isMapView,
                onToggle = { viewModel.toggleView() }
            )
        }
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize(),
        ) {
            when (hostsUiState) {
                is HostsUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
                is HostsUiState.Success -> {
                    if (isMapView) {
                        HostsMapScreen(
                            hosts = hostsUiState.hosts,
                            modifier = modifier
                        )
                    } else {
                        HostsListScreen(
                            hostsUiState.hosts,
                            isPaginating = isPaginating,
                            onLoadMore = { viewModel.loadMoreHosts() },
                            modifier = modifier.fillMaxWidth()
                        )
                    }
                }
                is HostsUiState.Error -> ErrorScreen( error = "Failed to load", modifier = modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun HostsListScreen(
    hosts: List<Host>,
    modifier: Modifier = Modifier,
    isPaginating: Boolean,
    onLoadMore: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val totalItems = layoutInfo.totalItemsCount
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                if (lastVisibleItem >= totalItems - 3) {
                    onLoadMore()
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        items(hosts, key = { it.id }) { host ->
            HostCard(
                host = host,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
            )
        }

        if (isPaginating) {
            item {
                LoadingItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun LoadingItem(modifier: Modifier = Modifier) {
    androidx.compose.material3.CircularProgressIndicator(modifier = modifier)
}

package com.app.workahomie.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.Column
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
import androidx.navigation.NavController
import com.app.workahomie.data.Host
import com.app.workahomie.models.HostViewModel
import com.app.workahomie.models.HostsUiState
import com.app.workahomie.ui.components.HostCard
import com.app.workahomie.ui.components.LoadingItem
import com.app.workahomie.ui.components.SearchBar
import com.app.workahomie.ui.components.ToggleViewButton
import com.google.gson.Gson

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HostsScreen(
    modifier: Modifier = Modifier,
    viewModel: HostViewModel = viewModel(),
    navController: NavController,
) {
    val hostsUiState = viewModel.hostsUiState
    val isPaginating = viewModel.isPaginating
    val isMapView = viewModel.isMapView
    val selectedLocation = viewModel.selectedLocation.value

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let {
            viewModel.refreshHosts()
        }
    }

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                SearchBar(
                    onPlaceSelected = { placeId ->
                        viewModel.fetchLatLngFromPlaceId(placeId, context = navController.context)
                    }
                )

                when (hostsUiState) {
                    is HostsUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
                    is HostsUiState.Success -> {
                        if (isMapView) {
                            HostsMapScreen(
                                hosts = hostsUiState.hosts,
                                modifier = modifier,
                                onHostClick = { host ->
                                    val hostJson = Uri.encode(Gson().toJson(host))
                                    navController.navigate("hostDetails/$hostJson")
                                },
                            )
                        } else {
                            HostsListScreen(
                                hostsUiState.hosts,
                                isPaginating = isPaginating,
                                onLoadMore = { viewModel.loadMoreHosts() },
                                onHostClick = { host ->
                                    val hostJson = Uri.encode(Gson().toJson(host))
                                    navController.navigate("hostDetails/$hostJson")
                                },
                                modifier = modifier.fillMaxWidth(),
                            )
                        }
                    }
                    is HostsUiState.Error -> ErrorScreen( error = hostsUiState.message, modifier = modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun HostsListScreen(
    hosts: List<Host>,
    modifier: Modifier = Modifier,
    onHostClick: (Host) -> Unit,
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
                    .fillMaxWidth(),
                onClick = { onHostClick(host) },
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

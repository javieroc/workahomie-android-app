package com.app.workahomie.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.workahomie.data.Host
import com.app.workahomie.models.WishlistUiState
import com.app.workahomie.models.WishlistViewModel
import com.app.workahomie.ui.components.LoadingItem
import com.app.workahomie.ui.components.WishlistCard
import com.google.gson.Gson

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WishlistScreen(
    modifier: Modifier = Modifier,
    viewModel: WishlistViewModel = viewModel(),
    navController: NavController,
) {
    val uiState = viewModel.wishlistUiState
    val isPaginating = viewModel.isPaginating

    LaunchedEffect(Unit) {
        viewModel.refreshWishlist()
    }

    Scaffold {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Wishlist",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .align(Alignment.Start)
                )
                when (uiState) {
                    is WishlistUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
                    is WishlistUiState.Success -> Wishlist(
                        hosts = uiState.hosts,
                        isPaginating = isPaginating,
                        onLoadMore = { viewModel.loadMoreHosts() },
                        onHostClick = { host ->
                            val hostJson = Uri.encode(Gson().toJson(host))
                            navController.navigate("hostDetails/$hostJson")
                        },
                    )
                    is WishlistUiState.Error -> ErrorScreen(error = "Could not load wishlist")
                }
            }
        }
    }
}

@Composable
fun Wishlist(
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
            .fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(hosts, key = { it.id }) { host ->
            WishlistCard(
                host=host,
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

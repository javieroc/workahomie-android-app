package com.app.workahomie.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
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
import com.app.workahomie.data.WishlistHost
import com.app.workahomie.models.WishlistUiState
import com.app.workahomie.models.WishlistViewModel
import com.app.workahomie.ui.components.LoadingItem
import com.app.workahomie.ui.components.WishlistCard

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WishlistScreen(viewModel: WishlistViewModel = viewModel(), modifier: Modifier = Modifier,) {
    val uiState = viewModel.wishlistUiState
    val isPaginating = viewModel.isPaginating

    LaunchedEffect(Unit) {
        viewModel.refreshWishlist()
    }

    Scaffold { padding ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            when (uiState) {
                is WishlistUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
                is WishlistUiState.Success -> Wishlist(
                    hosts = uiState.hosts,
                    isPaginating = isPaginating,
                    onLoadMore = { viewModel.loadMoreWishlistHosts() }
                )
                is WishlistUiState.Error -> ErrorScreen(error = "Could not load wishlist")
            }
        }
    }
}

@Composable
fun Wishlist(
    hosts: List<WishlistHost>,
    isPaginating: Boolean,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
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
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(hosts, key = { it.id }) { host ->
            WishlistCard(host)
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

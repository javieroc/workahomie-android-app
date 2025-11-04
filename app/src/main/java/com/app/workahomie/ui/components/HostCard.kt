package com.app.workahomie.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import coil3.compose.AsyncImage
import com.app.workahomie.R
import com.app.workahomie.data.Host
import com.app.workahomie.data.WishlistDto
import com.app.workahomie.network.HostApi
import com.app.workahomie.utils.parseAddress
import kotlinx.coroutines.launch

@SuppressLint("DefaultLocale")
@Composable
fun HostCard(
    host: Host,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isWishlisted by remember { mutableStateOf(host.isWishlisted ?: false) }
    var isProcessing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val images = host.profileImages + host.pictures
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { images.size })
    val textColor = MaterialTheme.colorScheme.onSurface

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .width(250.dp)
            .wrapContentHeight()
            .clickable { onClick() }
    ) {
        Column {
            Box {
                if (images.isNotEmpty()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(310.dp)
                    ) { page ->
                        AsyncImage(
                            model = images[page],
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    AsyncImage(
                        model = R.drawable.ic_broken_image,
                        contentDescription = "No image available",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(310.dp)
                    )
                }

                IconButton(
                    onClick = {
                        if (isProcessing) return@IconButton

                        isProcessing = true
                        isWishlisted = !isWishlisted // Optimistic update

                        coroutineScope.launch {
                            try {
                                val dto = WishlistDto(host.id)
                                if (isWishlisted) {
                                    HostApi.retrofitService.addToWishlist(dto)
                                } else {
                                    HostApi.retrofitService.removeFromWishlist(dto)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                isWishlisted = !isWishlisted // Rollback on failure
                            } finally {
                                isProcessing = false
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isWishlisted) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
                        ),
                        contentDescription = "Favorite",
                        tint = Color.White
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                ) {
                    repeat(images.size) { index ->
                        val selected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .size(if (selected) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (selected) Color.White else Color.Gray)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${host.firstName} ${host.lastName}",
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${String.format("%.1f", host.rate)} (${host.countReviews})",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                Text(text = host.occupation, style = MaterialTheme.typography.bodySmall, color = textColor)
                Text(text = parseAddress(host.address).displayName, style = MaterialTheme.typography.bodySmall, color = textColor)
            }
        }
    }
}

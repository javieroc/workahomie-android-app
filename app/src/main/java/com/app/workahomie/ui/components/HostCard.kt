package com.app.workahomie.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun HostCard(
    host: Host,
    modifier: Modifier = Modifier
) {
    var isFavorited by remember { mutableStateOf(false) }
    val images = host.profileImages + host.pictures
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { images.size })

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .width(250.dp)
            .wrapContentHeight()
            .clickable { /* handle click */ }
    ) {
        Column {
            Box {
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

                IconButton(
                    onClick = { isFavorited = !isFavorited },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isFavorited) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
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
                        color = Color(0xff0a0a0a)
                    )
                    Text(
                        text = "★ 4.3 (10)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Text(text = host.occupation, style = MaterialTheme.typography.bodySmall, color = Color(0xff0a0a0a))
                Text(text = host.address, style = MaterialTheme.typography.bodySmall, color = Color(0xff0a0a0a))
            }
        }
    }
}

package com.app.workahomie.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.workahomie.R

@Composable
fun WelcomeScreen(onLoginClick: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 5 })

    val slides = listOf(
        "Welcome to Workahomies — your coworking companion",
        "Discover inspiring workspaces wherever you go",
        "Meet remote professionals and collaborate nearby",
        "Stay productive while building real connections",
        "Work, chat, and grow your network easily"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.splash),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xAA805AD5), // 0xAA adds transparency (~67%)
                            Color(0xAAD53F8C)  // transparent pink
                        )
                    )
                )
        )

        // Transparent pager with white text
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Workahomie logo",
                modifier = Modifier
                    .size(220.dp)
                    .padding(bottom = 32.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
            )

            HorizontalPager(state = pagerState) { page ->
                Text(
                    text = slides[page],
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    lineHeight = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pager dots
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(slides.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (isSelected) 10.dp else 8.dp)
                            .background(
                                if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLoginClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFFD53F8C)
                ),
                modifier = Modifier
                    .width(220.dp)
                    .height(50.dp)
            ) {
                Text(text = "Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

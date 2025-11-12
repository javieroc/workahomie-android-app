package com.app.workahomie.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.FlowRow
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.app.workahomie.R
import com.app.workahomie.constants.facilityIcons
import com.app.workahomie.data.Host
import com.app.workahomie.models.AuthViewModel
import com.app.workahomie.models.CreateRequestUiState
import com.app.workahomie.models.HostViewModel
import com.app.workahomie.ui.components.RequestToStayForm
import com.app.workahomie.utils.parseAddress


@Composable
fun HostDetailsScreen(
    host: Host,
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    hostViewModel: HostViewModel = viewModel(),
) {
    val images = host.pictures
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { images.size })
    val context = LocalContext.current
    val createRequestUiState = hostViewModel.createRequestUiState
    val textColor = MaterialTheme.colorScheme.onSurface

    LaunchedEffect(createRequestUiState) {
        when (createRequestUiState) {
            is CreateRequestUiState.Success -> {
                Toast.makeText(context, "Request sent successfully!", Toast.LENGTH_SHORT).show()
                hostViewModel.resetCreateRequestState()
            }
            is CreateRequestUiState.Error -> {
                Toast.makeText(context, createRequestUiState.message, Toast.LENGTH_SHORT).show()
                hostViewModel.resetCreateRequestState()
            }
            else -> {}
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Box {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) { page ->
                    AsyncImage(
                        model = images[page],
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(8.dp)
                        .clickable {
                            navController.popBackStack()
                        }
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
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

                Text(
                    text = "${pagerState.currentPage + 1} / ${images.size}",
                    color = textColor,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        item {
            Column {
                Text(
                    text = parseAddress(host.address).displayName,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = host.placeDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }
        }

        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "4.3 (10) reviews",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        item {
            Column {
                Text(
                    text = "Facilities",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    host.facilities.forEach { facility ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            facilityIcons[facility]?.let { icon ->
                                Icon(
                                    imageVector = icon,
                                    contentDescription = facility,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = facility.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item { HorizontalDivider() }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = host.profileImages.firstOrNull() ?: R.drawable.ic_broken_image,
                    contentDescription = "Host avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Column {
                    Text(
                        text = "${host.firstName} ${host.lastName}",
                    )
                    Text(text = host.occupation)
                    host.phone?.let { phoneNumber ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse("https://wa.me/$phoneNumber")
                                }
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "WhatsApp",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = phoneNumber)
                        }
                    }
                }
            }
        }

        item {
            if (createRequestUiState is CreateRequestUiState.Loading) {
                LoadingScreen()
            } else {
                RequestToStayForm(
                    onSubmit = { checkIn, checkOut, message ->
                        val userProfile = authViewModel.userProfile
                        hostViewModel.createRequest(
                            hostId = host.id,
                            checkIn = checkIn,
                            checkOut = checkOut,
                            message = message,
                            userName = userProfile?.name,
                            userEmail = userProfile?.email,
                            userAvatar = userProfile?.pictureURL
                        )
                    }
                )
            }
        }
    }
}

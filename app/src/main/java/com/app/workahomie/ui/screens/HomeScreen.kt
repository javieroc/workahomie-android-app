package com.app.workahomie.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.app.workahomie.models.MarsUiState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    marsUiState: MarsUiState,
    modifier: Modifier = Modifier,
) {
    Scaffold {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            when (marsUiState) {
                is MarsUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
                is MarsUiState.Success -> ResultScreen(
                    marsUiState.photos, modifier = modifier.fillMaxWidth()
                )
                is MarsUiState.Error -> ErrorScreen( error = "Failed to load", modifier = modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun ResultScreen(photos: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(text = photos)
    }
}

package com.javohir.masterbektask.presentation.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.javohir.masterbektask.domain.model.ConversationState
import com.javohir.masterbektask.presentation.components.SpeechIndicator
import com.javohir.masterbektask.presentation.components.StartChatButton
import com.javohir.masterbektask.presentation.components.VideoPlayerView
/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.presentation.conversation
 * Description: Screen
 */

@Composable
fun ConversationScreen(
    viewModel: ConversationViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current


    DisposableEffect(lifecycleOwner) {
        viewModel.observeLifecycle(lifecycleOwner)
        onDispose {}
    }


    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is ConversationEvent.StartListening -> {}
                is ConversationEvent.StopListening -> {}
                is ConversationEvent.ShowError -> {}
            }
        }
    }

    ConversationContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}

@Composable
private fun ConversationContent(
    uiState: ConversationUiState,
    onAction: (ConversationIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (uiState.currentVideoUri == null) Color.White else Color.Transparent)
    ) {
        if (uiState.currentVideoUri != null) {
            VideoPlayerView(
                uri = uiState.currentVideoUri,
                isLooping = uiState.isLooping,
                modifier = Modifier.fillMaxSize(),
                onVideoEnded = {
                    onAction(ConversationIntent.VideoEnded)
                }
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp),
                color = Color.White
            )
        }

        if (uiState.conversationState == ConversationState.IDLE && uiState.currentVideoUri != null) {
            StartChatButton(
                onClick = { onAction(ConversationIntent.StartChat) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
            )
        }


        if (uiState.conversationState == ConversationState.LISTENING) {
            SpeechIndicator(
                isListening = uiState.isListening,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ConversationPreview() {
    ConversationContent(
        uiState = ConversationUiState(),
        onAction = {},
        modifier = Modifier.fillMaxSize()
    )
}
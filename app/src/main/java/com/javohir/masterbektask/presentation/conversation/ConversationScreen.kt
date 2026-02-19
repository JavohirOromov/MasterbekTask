package com.javohir.masterbektask.presentation.conversation
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    DisposableEffect(lifecycleOwner) {
        viewModel.observeLifecycle(lifecycleOwner)
        onDispose {}
    }


    var showNoInternetDialog by remember { mutableStateOf(false) }
    var showMicrophoneErrorDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is ConversationEvent.StartListening -> {}
                is ConversationEvent.StopListening -> {}
                is ConversationEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is ConversationEvent.ShowNoInternetDialog -> {
                    showNoInternetDialog = true
                }
                is ConversationEvent.ShowMicrophoneError -> {
                    showMicrophoneErrorDialog = event.message
                }
            }
        }
    }

    if (showNoInternetDialog) {
        AlertDialog(
            onDismissRequest = { showNoInternetDialog = false },
            title = { Text("Internet yo'q") },
            text = { Text("Internet'ga ulaning") },
            confirmButton = {
                TextButton(onClick = { showNoInternetDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    showMicrophoneErrorDialog?.let { message ->
        AlertDialog(
            onDismissRequest = { showMicrophoneErrorDialog = null },
            title = { Text("Mikrofon") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { showMicrophoneErrorDialog = null }) {
                    Text("OK")
                }
            }
        )
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
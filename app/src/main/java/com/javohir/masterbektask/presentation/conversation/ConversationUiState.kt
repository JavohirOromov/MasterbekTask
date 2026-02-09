package com.javohir.masterbektask.presentation.conversation
import android.net.Uri
import com.javohir.masterbektask.domain.model.ConversationState
/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.presentation.conversation
 * Description: UiState
 */
data class ConversationUiState(
    val currentVideoUri: Uri? = null,
    val isLooping: Boolean = true,
    val conversationState: ConversationState = ConversationState.IDLE,
    val isListening: Boolean = false,
)

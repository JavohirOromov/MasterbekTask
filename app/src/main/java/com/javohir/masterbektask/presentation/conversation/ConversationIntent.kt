package com.javohir.masterbektask.presentation.conversation

/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.presentation.conversation
 * Description: User Action
 */
sealed class ConversationIntent {
    object VideoEnded : ConversationIntent()
    object StartChat : ConversationIntent()
    data class SpeechResult(val text: String) : ConversationIntent()
    object SpeechError : ConversationIntent()
}
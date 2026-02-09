package com.javohir.masterbektask.presentation.conversation

/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.presentation.conversation
 * Description: Event
 */

sealed class ConversationEvent {
    object StartListening : ConversationEvent()
    object StopListening : ConversationEvent()
    data class ShowError(val message: String) : ConversationEvent()
}
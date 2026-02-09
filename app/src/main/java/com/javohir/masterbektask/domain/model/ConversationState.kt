package com.javohir.masterbektask.domain.model

/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.domain.model
 * Description:
 */
enum class ConversationState {
    IDLE,           // Kutish holati
    GREETING,       // Salomlashish
    LISTENING,      // Tinglash
    RESPONDING,     // Javob berish
    GOODBYE,        // Xayrlashish
    ERROR           // Xatolik
}

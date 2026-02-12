package com.javohir.masterbektask.domain.model

/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.domain.model
 * Description:
 */
data class VideoResponse(
    val videoType: VideoType,
    val conversationState: ConversationState,
    val uri: String
)

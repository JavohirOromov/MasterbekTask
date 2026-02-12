package com.javohir.masterbektask.domain.repository

import com.javohir.masterbektask.domain.model.VideoType

/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.domain.repository
 * Description: Repository Interface
 */
interface ConversationRepository {
    suspend fun getVideoUri(videoType: VideoType): String?
    suspend fun getAllVideoUris(): Map<VideoType, String>
}
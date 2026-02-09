package com.javohir.masterbektask.domain.repository

import android.net.Uri
import com.javohir.masterbektask.domain.model.VideoType

/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.domain.repository
 * Description: Repository Interface
 */
interface ConversationRepository {
    suspend fun getVideoUri(videoType: VideoType): Uri?
    suspend fun getAllVideoUris(): Map<VideoType, Uri>
}
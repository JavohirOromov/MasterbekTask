package com.javohir.masterbektask.domain.useCase
import android.util.Log
import com.javohir.masterbektask.domain.model.ConversationState
import com.javohir.masterbektask.domain.model.VideoResponse
import com.javohir.masterbektask.domain.model.VideoType
import com.javohir.masterbektask.domain.repository.ConversationRepository
import javax.inject.Inject

/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.domain.useCase
 * Description: UseCase
 */
class GetVideoForKeywordUseCase @Inject constructor(
    private val repository: ConversationRepository,
    private val detectKeywordUseCase: DetectKeywordUseCase
) {

    suspend fun getVideoForKeyword(text: String): VideoResponse? {
        Log.d("GetVideoForKeywordUseCase", " Text tahlil qilinmoqda: \"$text\"")

        val videoType = detectKeywordUseCase.detectKeyword(text)
        if (videoType == null) {
            Log.e("GetVideoForKeywordUseCase", " VideoType topilmadi - detectKeyword null qaytdi")
            return null
        }
        Log.d("GetVideoForKeywordUseCase", " VideoType topildi: $videoType")

        val uri = repository.getVideoUri(videoType)
        if (uri == null) {
               Log.e("GetVideoForKeywordUseCase", " URI topilmadi - repository.getVideoUri null qaytdi")
            return null
        }
        Log.d("GetVideoForKeywordUseCase", " URI topildi: $uri")

        val conversationState = getConversationStateForVideoType(videoType)
             Log.d("GetVideoForKeywordUseCase", " ConversationState: $conversationState")

        return VideoResponse(
            videoType = videoType,
            conversationState = conversationState,
            uri = uri
        )
    }

    fun getConversationStateForVideoType(videoType: VideoType): ConversationState {
        return when (videoType) {
            VideoType.IDLE -> ConversationState.IDLE
            VideoType.GREETING -> ConversationState.GREETING
            VideoType.LISTENING -> ConversationState.LISTENING
            VideoType.GOODBYE -> ConversationState.GOODBYE
            VideoType.FALLBACK -> ConversationState.ERROR
            VideoType.WEATHER,
            VideoType.GENERAL_RESPONSE,
            VideoType.PROMPT -> ConversationState.RESPONDING
        }
    }

    fun shouldLoop(videoType: VideoType): Boolean {
        return when (videoType) {
            VideoType.IDLE,
            VideoType.LISTENING -> true
            else -> false
        }
    }
    

    suspend fun getIdleVideo(): VideoResponse? {
        val uri = repository.getVideoUri(VideoType.IDLE) ?: return null
        return VideoResponse(
            videoType = VideoType.IDLE,
            conversationState = ConversationState.IDLE,
            uri = uri
        )
    }
    
    suspend fun getGreetingVideo(): VideoResponse? {
        val uri = repository.getVideoUri(VideoType.GREETING) ?: return null
        return VideoResponse(
            videoType = VideoType.GREETING,
            conversationState = ConversationState.GREETING,
            uri = uri
        )
    }
    
    suspend fun getListeningVideo(): VideoResponse? {
        val uri = repository.getVideoUri(VideoType.LISTENING) ?: return null
        return VideoResponse(
            videoType = VideoType.LISTENING,
            conversationState = ConversationState.LISTENING,
            uri = uri
        )
    }
    
    suspend fun getFallbackVideo(): VideoResponse? {
        val uri = repository.getVideoUri(VideoType.FALLBACK) ?: return null
        return VideoResponse(
            videoType = VideoType.FALLBACK,
            conversationState = ConversationState.ERROR,
            uri = uri
        )
    }

    suspend fun preloadAllVideos() {
        repository.getAllVideoUris()
    }
}
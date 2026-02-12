package com.javohir.masterbektask.domain.useCase
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

    suspend fun preloadAllVideos(): Map<VideoType, String> {
        return repository.getAllVideoUris()
    }

    suspend fun getGreetingVideo(): VideoResponse? = videoResponseFor(VideoType.GREETING)
    suspend fun getIdleVideo(): VideoResponse? = videoResponseFor(VideoType.IDLE)
    suspend fun getListeningVideo(): VideoResponse? = videoResponseFor(VideoType.LISTENING)
    suspend fun getFallbackVideo(): VideoResponse? = videoResponseFor(VideoType.FALLBACK)

    suspend fun getVideoForKeyword(text: String): VideoResponse? {
        val videoType = detectKeywordUseCase.detectKeyword(text) ?: return null
        return videoResponseFor(videoType)
    }

    fun shouldLoop(videoType: VideoType): Boolean = videoType == VideoType.IDLE || videoType == VideoType.LISTENING

    private suspend fun videoResponseFor(videoType: VideoType): VideoResponse? {
        val uri = repository.getVideoUri(videoType) ?: return null
        val conversationState = when (videoType) {
            VideoType.IDLE -> ConversationState.IDLE
            VideoType.GREETING -> ConversationState.GREETING
            VideoType.LISTENING, VideoType.PROMPT -> ConversationState.LISTENING
            VideoType.WEATHER, VideoType.GENERAL_RESPONSE -> ConversationState.RESPONDING
            VideoType.GOODBYE -> ConversationState.GOODBYE
            VideoType.FALLBACK -> ConversationState.ERROR
        }
        return VideoResponse(videoType = videoType, conversationState = conversationState, uri = uri)
    }
}

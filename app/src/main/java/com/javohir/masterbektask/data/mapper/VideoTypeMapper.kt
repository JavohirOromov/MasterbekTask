package com.javohir.masterbektask.data.mapper

import com.javohir.masterbektask.domain.model.VideoType

/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.data.mapper
 * Description: Mapper class
 */
object VideoTypeMapper {
    fun getFileName(videoType: VideoType): String{
        return when(videoType){
            VideoType.IDLE -> "idle.mp4"
            VideoType.GREETING -> "greeting.mp4"
            VideoType.LISTENING -> "listening.mp4"
            VideoType.WEATHER -> "weather.mp4"
            VideoType.GENERAL_RESPONSE -> "general_response.mp4"
            VideoType.GOODBYE -> "goodbye.mp4"
            VideoType.FALLBACK -> "fallback.mp4"
            VideoType.PROMPT -> "prompt.mp4"
        }
    }

    fun getAllFileNames(): List<String> {
        return VideoType.entries.map { getFileName(it) }
    }

    fun getVideoTypeFromFileName(fileName: String): VideoType? {
        return VideoType.entries.find { getFileName(it) == fileName }
    }
}
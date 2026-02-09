package com.javohir.masterbektask.data.repository

import android.net.Uri
import android.util.Log
import com.javohir.masterbektask.data.local.VideoAssetProvider
import com.javohir.masterbektask.data.mapper.VideoTypeMapper
import com.javohir.masterbektask.domain.model.VideoType
import com.javohir.masterbektask.domain.repository.ConversationRepository
import jakarta.inject.Inject

/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.data.repository
 * Description: Repository Impelementatsiya
 */
class ConversationRepositoryImpl @Inject constructor(
    private val videoAssetProvider: VideoAssetProvider,
    private val videoTypeMapper: VideoTypeMapper
): ConversationRepository {

    override suspend fun getVideoUri(videoType: VideoType): Uri? {
        val fileName = videoTypeMapper.getFileName(videoType)
        Log.d("ConversationRepositoryImpl", " VideoType: $videoType -> FileName: $fileName")
        val uri = videoAssetProvider.getVideoUri(fileName)
        if (videoType == VideoType.GENERAL_RESPONSE || videoType == VideoType.WEATHER) {
            Log.d("DebugWeather", "getVideoUri: videoType=$videoType fileName=$fileName uri=$uri")
        }
        if (uri == null) {
            Log.e("ConversationRepositoryImpl", "   VideoType: $videoType, FileName: $fileName")
        } else {
            Log.d("ConversationRepositoryImpl", " URI topildi: $uri")
        }
        return uri
    }

    override suspend fun getAllVideoUris(): Map<VideoType, Uri> {

        val allFileNames = videoTypeMapper.getAllFileNames()
        

        val fileNameToUriMap = videoAssetProvider.getAllVideoUris(allFileNames)
        

        return fileNameToUriMap
            .mapNotNull { (fileName, uri) ->
                val videoType = videoTypeMapper.getVideoTypeFromFileName(fileName)
                if (videoType != null && uri != null) {
                    videoType to uri
                } else {
                    null
                }
            }
            .toMap()
    }
}
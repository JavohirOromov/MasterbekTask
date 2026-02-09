package com.javohir.masterbektask.data.local
import android.content.Context
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import com.javohir.masterbektask.R
import androidx.core.net.toUri

/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.data.local
 * Description: Video assetlarni boshqarish
 */
class VideoAssetProvider @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun getVideoUri(fileName: String): Uri? {
        return try {
            Log.d("VideoAssetProvider", " FileName: $fileName")
            val resourceId = when (fileName) {
                "idle.mp4" -> R.raw.idle
                "greeting.mp4" -> R.raw.greeting
                "listening.mp4" -> R.raw.listening
                "weather.mp4" -> R.raw.weather
                "general_response.mp4" -> R.raw.general_response
                "goodbye.mp4" -> R.raw.goodbye
                "fallback.mp4" -> R.raw.fallback
                "prompt.mp4" -> R.raw.prompt
                else -> {
                    Log.e("VideoAssetProvider", " Noma'lum fileName: $fileName")
                    return null
                }
            }
            Log.d("VideoAssetProvider", " ResourceId topildi: $resourceId")
            val uri = "android.resource://${context.packageName}/$resourceId".toUri()
            Log.d("VideoAssetProvider", " URI yaratildi: $uri")
            uri
        } catch (e: Exception) {
            Log.e("VideoAssetProvider", " Exception: ${e.message}", e)
            null
        }
    }
    fun getAllVideoUris(fileNames: List<String>): Map<String,Uri?>{
        return fileNames.associateWith { getVideoUri(fileName = it) }
    }
}
package com.javohir.masterbektask.domain.useCase
import android.util.Log
import com.javohir.masterbektask.domain.model.VideoType
import javax.inject.Inject

/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.domain.useCase
 * Description: UseCase
 */
class DetectKeywordUseCase @Inject constructor() {

    fun detectKeyword(text: String): VideoType?{
        val lowerText = text.lowercase().trim()
        Log.d("DetectKeywordUseCase", " Text tahlil qilinmoqda: \"$text\" -> \"$lowerText\"")

        if (lowerText.isBlank()){
            Log.w("DetectKeywordUseCase", " Text bo'sh yoki faqat bo'shliq")
            return null
        }

        val result = when {
            // Goodbye kalit so'zlari
            containsKeyword(lowerText, listOf("goodbye", "bye", "see you", "farewell", "good bye")) -> {
                Log.d("DetectKeywordUseCase", " GOODBYE kalit so'zi topildi")
                VideoType.GOODBYE
            }

            // Greeting kalit so'zlari
            containsKeyword(lowerText, listOf("hello", "hi", "hey", "greetings", "good morning", "good afternoon")) -> {
                Log.d("DetectKeywordUseCase", " GREETING kalit so'zi topildi")
                VideoType.GREETING
            }

            // Weather kalit so'zlari
            containsKeyword(lowerText, listOf("weather", "today", "rain", "sunny", "cloudy", "temperature", "hot", "cold")) -> {
                Log.d("DetectKeywordUseCase", " WEATHER kalit so'zi topildi")
                VideoType.WEATHER
            }
            // Boshqa hamma narsa - general response
            else -> {
                Log.d("DetectKeywordUseCase", " GENERAL_RESPONSE (kalit so'z topilmadi)")
                VideoType.GENERAL_RESPONSE
            }
        }
        
        Log.d("DetectKeywordUseCase", " VideoType qaytarilmoqda: $result")
        return result
    }

    private fun containsKeyword(text: String, keywords: List<String>): Boolean{
        return keywords.any { keyword ->
            text.contains(keyword,ignoreCase = true)
        }
    }
}
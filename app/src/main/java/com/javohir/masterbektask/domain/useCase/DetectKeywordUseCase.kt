package com.javohir.masterbektask.domain.useCase

import com.javohir.masterbektask.domain.model.VideoType
import javax.inject.Inject

/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.domain.useCase
 * Description: UseCase
 */
class DetectKeywordUseCase @Inject constructor() {

    fun detectKeyword(text: String): VideoType? {
        val lowerText = text.lowercase().trim()

        if (lowerText.isBlank()) {
            return null
        }

        val result = when {
            // Goodbye kalit so'zlari
            containsKeyword(lowerText, listOf("goodbye", "bye", "see you", "farewell", "good bye")) -> {
                VideoType.GOODBYE
            }

            // Greeting kalit so'zlari
            containsKeyword(lowerText, listOf("hello", "hi", "hey", "greetings", "good morning", "good afternoon")) -> {
                VideoType.GREETING
            }

            containsKeyword(lowerText, listOf("weather", "rain", "sunny", "cloudy", "temperature", "forecast")) -> {
                VideoType.WEATHER
            }
            // Boshqa hamma narsa - general response
            else -> {
                VideoType.GENERAL_RESPONSE
            }
        }
        
        return result
    }

    private fun containsKeyword(text: String, keywords: List<String>): Boolean {
        return keywords.any { keyword ->
            text.contains(keyword, ignoreCase = true)
        }
    }
}
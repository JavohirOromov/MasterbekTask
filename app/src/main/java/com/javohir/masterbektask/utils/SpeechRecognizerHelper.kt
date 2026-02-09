package com.javohir.masterbektask.utils
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.utils
 * Description: SpeechRecognizer Helper - Android SpeechRecognizer API wrapper
 */


class SpeechRecognizerHelper @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    /** Natija allaqachon yetkazilgan bo'lsa, stopListening() dan keyin keladigan ERROR_CLIENT ni e'tiborsiz qilamiz */
    private var resultAlreadyDelivered = false

    private var onResultCallback: ((String) -> Unit)? = null
    private var onErrorCallback: ((String) -> Unit)? = null
    private var onListeningChangedCallback: ((Boolean) -> Unit)? = null


    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }


    fun startListening(
        onResult: (String) -> Unit,
        onError: (String) -> Unit,
        onListeningChanged: (Boolean) -> Unit
    ) {
        if (!isAvailable()) {
            Log.e("SpeechRecognizerHelper", "Speech recognition mavjud emas")
            onError("Speech recognition mavjud emas")
            return
        }

        if (isListening) {
            stopListening()
        }

        this.onResultCallback = onResult
        this.onErrorCallback = onError
        this.onListeningChangedCallback = onListeningChanged
        this.resultAlreadyDelivered = false

        Log.d("SpeechRecognizerHelper", "Speech recognition boshlanmoqda...")

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(createRecognitionListener())


        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US") // Ingliz tili
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer?.startListening(intent)
        isListening = true
        onListeningChangedCallback?.invoke(true)
        Log.d("SpeechRecognizerHelper", "Mikrofon yoqildi - ovozni tinglash boshlandi")
    }


    fun stopListening() {
        Log.d("SpeechRecognizerHelper", "Speech recognition to'xtatilmoqda...")
        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()
        isListening = false
        onListeningChangedCallback?.invoke(false)
        Log.d("SpeechRecognizerHelper", "Mikrofon o'chirildi")
    }


    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("SpeechRecognizerHelper", "Tayyor - ovozni qabul qilishga tayyor")
            }

            override fun onBeginningOfSpeech() {
                Log.d("SpeechRecognizerHelper", "Ovoz boshlanmoqda...")
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Ovoz balandligi o'zgarishi (ixtiyoriy log)
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Buffer qabul qilindi (ixtiyoriy log)
            }

            override fun onEndOfSpeech() {
                Log.d("SpeechRecognizerHelper", "Ovoz tugadi - tahlil qilinmoqda...")
                isListening = false
                onListeningChangedCallback?.invoke(false)
            }

            override fun onError(error: Int) {
                isListening = false
                onListeningChangedCallback?.invoke(false)

                // Natija allaqachon yetkazilgan bo'lsa va stopListening() tufayli ERROR_CLIENT kelsa, e'tiborsiz qilamiz
                if (error == SpeechRecognizer.ERROR_CLIENT && resultAlreadyDelivered) {
                    Log.d("SpeechRecognizerHelper", "ERROR_CLIENT e'tiborsiz qilindi - natija allaqachon yetkazilgan")
                    return
                }

                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio xatolik"
                    SpeechRecognizer.ERROR_CLIENT -> "Client xatolik"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Ruxsat yetarli emas"
                    SpeechRecognizer.ERROR_NETWORK -> "Tarmoq xatolik"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Tarmoq timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "Hech narsa topilmadi"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer band"
                    SpeechRecognizer.ERROR_SERVER -> "Server xatolik"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Nutq timeout"
                    else -> "Noma'lum xatolik"
                }

                Log.e("SpeechRecognizerHelper", "Xatolik: $errorMessage (code: $error)")
                onErrorCallback?.invoke(errorMessage)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val confidenceScores = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)

                if (!matches.isNullOrEmpty()) {
                    val bestMatch = matches[0]
                    val confidence = if (confidenceScores != null && confidenceScores.isNotEmpty()) {
                        confidenceScores[0]
                    } else {
                        null
                    }
                    
                    // Barcha variantlarni log qilish
                    Log.d("SpeechRecognizerHelper", "═══════════════════════════════════════")
                    Log.d("SpeechRecognizerHelper", "🎤 OVOZ TEXT'GA AYLANDI!")
                    Log.d("SpeechRecognizerHelper", "📝 To'liq natija: $bestMatch")
                    if (confidence != null) {
                        Log.d("SpeechRecognizerHelper", "📊 Ishonchlilik: ${(confidence * 100).toInt()}%")
                    }
                    if (matches.size > 1) {
                        Log.d("SpeechRecognizerHelper", "📋 Boshqa variantlar:")
                        matches.forEachIndexed { index, match ->
                            if (index > 0) {
                                val conf = if (confidenceScores != null && index < confidenceScores.size) {
                                    confidenceScores[index]
                                } else null
                                Log.d("SpeechRecognizerHelper", "   $index. $match ${if (conf != null) "(${(conf * 100).toInt()}%)" else ""}")
                            }
                        }
                    }
                    Log.d("SpeechRecognizerHelper", "═══════════════════════════════════════")
                    
                    resultAlreadyDelivered = true
                    onResultCallback?.invoke(bestMatch)
                } else {
                    Log.w("SpeechRecognizerHelper", "⚠️ Natija topilmadi - bo'sh natija")
                    onErrorCallback?.invoke("Natija topilmadi")
                }

                isListening = false
                onListeningChangedCallback?.invoke(false)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    Log.d("SpeechRecognizerHelper", "🔄 Qisman natija: ${matches[0]}")
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // Event'lar (ixtiyoriy log)
            }
        }
    }

    fun release() {
        stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null


        onResultCallback = null
        onErrorCallback = null
        onListeningChangedCallback = null
    }

    fun isListening(): Boolean = isListening
}

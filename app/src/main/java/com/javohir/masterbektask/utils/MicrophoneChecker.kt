package com.javohir.masterbektask.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.speech.SpeechRecognizer
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.utils
 * Description: Mikrafon'ni tekshiradi
 */
class MicrophoneChecker @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun isMicrophoneAvailable(): Boolean {
        return hasPermission() && isSpeechRecognitionAvailable()
    }

    fun getUnavailableReason(): String? {
        if (!hasPermission()) return "Mikrofon ruxsati berilmagan"
        if (!isSpeechRecognitionAvailable()) return "Ovozni tanlash mavjud emas"
        return null
    }

    private fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isSpeechRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("sdk") || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for")
            || Build.MANUFACTURER.contains("Genymotion")
            || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
            || "generic" == Build.PRODUCT
            || Build.HARDWARE.contains("goldfish") || Build.HARDWARE.contains("ranchu")
            || Build.HARDWARE.contains("vbox86") || Build.HARDWARE.contains("vbox"))
    }
}

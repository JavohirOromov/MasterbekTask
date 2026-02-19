package com.javohir.masterbektask.utils
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.utils
 * Description: Permission Handler
 */

object PermissionHandler {

    fun hasMicrophonePermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }
}
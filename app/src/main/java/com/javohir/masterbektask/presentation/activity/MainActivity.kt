package com.javohir.masterbektask.presentation.activity

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.javohir.masterbektask.presentation.conversation.ConversationScreen
import com.javohir.masterbektask.presentation.theme.MasterbekTaskTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("MainActivity", "Microphone permission granted")
        } else {
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        

        requestMicrophonePermissionIfNeeded()
        
        setContent {
            MasterbekTaskTheme {
                ConversationScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
    

    private fun requestMicrophonePermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else { }
    }
}

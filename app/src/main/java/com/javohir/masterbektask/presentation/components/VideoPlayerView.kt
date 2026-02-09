package com.javohir.masterbektask.presentation.components
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.presentation.conversation
 * Description:
 */

@Composable
fun VideoPlayerView(
    uri: Uri?,
    isLooping: Boolean = false,
    modifier: Modifier = Modifier,
    onVideoEnded: () -> Unit = {}
) {
    val context = LocalContext.current


    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }


    LaunchedEffect(uri) {
        if (uri != null) {
            Log.d("VideoPlayerView", "Loading video: $uri, isLooping: $isLooping")

            exoPlayer.stop()
            exoPlayer.clearMediaItems()

            // Yangi video yuklash
            val mediaItem = MediaItem.fromUri(uri)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.repeatMode = if (isLooping) {
                Player.REPEAT_MODE_ONE
            } else {
                Player.REPEAT_MODE_OFF
            }
            exoPlayer.prepare()
            exoPlayer.play()
        } else {
            Log.d("VideoPlayerView", "URI is null")
        }
    }

    LaunchedEffect(isLooping) {
        exoPlayer.repeatMode = if (isLooping) {
            Player.REPEAT_MODE_ONE
        } else {
            Player.REPEAT_MODE_OFF
        }
    }


    DisposableEffect(uri, isLooping, onVideoEnded) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                Log.d("VideoPlayerView", "Playback state: $playbackState, isLooping: $isLooping")
                when (playbackState) {
                    Player.STATE_ENDED -> {
                        if (!isLooping) {
                            Log.d("VideoPlayerView", "Video ended (not looping), calling onVideoEnded")
                            onVideoEnded()
                        } else {
                            Log.d("VideoPlayerView", "Video ended but looping, not calling callback")
                        }
                    }
                    Player.STATE_READY -> {
                        Log.d("VideoPlayerView", "Video ready to play")
                    }
                    Player.STATE_BUFFERING -> {
                        Log.d("VideoPlayerView", "Video buffering")
                    }

                    Player.STATE_IDLE -> {

                    }
                }
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    // ExoPlayer View
    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { }
        )
    }

    DisposableEffect(Unit) {
        onDispose {

            exoPlayer.release()
        }
    }
}


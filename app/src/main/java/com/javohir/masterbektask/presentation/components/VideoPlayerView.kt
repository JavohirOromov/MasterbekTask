package com.javohir.masterbektask.presentation.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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

    val playerA = remember { ExoPlayer.Builder(context).build() }
    val playerB = remember { ExoPlayer.Builder(context).build() }
    var frontIndex by remember { mutableIntStateOf(0) }

    val activePlayer: ExoPlayer = if (frontIndex == 0) playerA else playerB

    LaunchedEffect(uri) {
        if (uri != null) {
            Log.d("VideoPlayerView", "Loading video (dual): $uri, frontIndex=$frontIndex")

            val back = if (frontIndex == 0) playerB else playerA
            val mediaItem = MediaItem.fromUri(uri)
            back.repeatMode = if (isLooping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
            back.setMediaItem(mediaItem)
            back.prepare()
            back.playWhenReady = false

            suspendCancellableCoroutine { cont ->
                val listener = object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            back.removeListener(this)
                            cont.resume(Unit)
                        }
                    }
                }
                back.addListener(listener)
                cont.invokeOnCancellation { back.removeListener(listener) }
            }

            back.playWhenReady = true
            frontIndex = 1 - frontIndex
            Log.d("VideoPlayerView", "Switched, frontIndex=$frontIndex")
        } else {
            Log.d("VideoPlayerView", "URI is null")
        }
    }

    LaunchedEffect(isLooping) {
        val mode = if (isLooping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        playerA.repeatMode = mode
        playerB.repeatMode = mode
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE, Lifecycle.Event.ON_STOP -> {
                    playerA.pause()
                    playerB.pause()
                }
                Lifecycle.Event.ON_RESUME, Lifecycle.Event.ON_START -> {
                    val active = if (frontIndex == 0) playerA else playerB
                    active.playWhenReady = true
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(uri, isLooping, onVideoEnded) {
        val listenerA = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED && frontIndex == 0 && !isLooping) {
                    Log.d("VideoPlayerView", "Video A ended")
                    onVideoEnded()
                }
            }
        }
        val listenerB = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED && frontIndex == 1 && !isLooping) {
                    Log.d("VideoPlayerView", "Video B ended")
                    onVideoEnded()
                }
            }
        }
        playerA.addListener(listenerA)
        playerB.addListener(listenerB)
        onDispose {
            playerA.removeListener(listenerA)
            playerB.removeListener(listenerB)
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = false
                    player = playerA
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { view ->
                view.player = activePlayer
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            playerA.release()
            playerB.release()
        }
    }
}

package com.javohir.masterbektask.presentation.conversation
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import androidx.core.net.toUri
import com.javohir.masterbektask.domain.model.ConversationState
import com.javohir.masterbektask.domain.useCase.GetVideoForKeywordUseCase
import android.speech.SpeechRecognizer
import com.javohir.masterbektask.utils.NetworkMonitor
import com.javohir.masterbektask.utils.SpeechRecognizerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.presentation.conversation
 * Description: ViewModel
 */
@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val getVideoForKeywordUseCase: GetVideoForKeywordUseCase,
    private val speechRecognizerHelper: SpeechRecognizerHelper,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _state = MutableStateFlow(ConversationUiState())
    val state: StateFlow<ConversationUiState> = _state.asStateFlow()


    private val _event = MutableSharedFlow<ConversationEvent>()
    val event: SharedFlow<ConversationEvent> = _event.asSharedFlow()

    init {
        loadIdleVideo()

        preloadAllVideos()
    }
    

    private fun preloadAllVideos() {
        viewModelScope.launch {
            try {
                val allVideos = getVideoForKeywordUseCase.preloadAllVideos()
            } catch (e: Exception) {

            }
        }
    }

    fun observeLifecycle(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> {
                        speechRecognizerHelper.stopListening()
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        val currentState = _state.value.conversationState
                        if (currentState == ConversationState.LISTENING) {
                            startListening()
                        }
                    }
                    else -> {}
                }
            }
        })
    }

    fun onAction(intent: ConversationIntent) {
        when (intent) {
            is ConversationIntent.StartChat -> handleStartChat()
            is ConversationIntent.SpeechResult -> handleSpeechResult(intent.text)
            is ConversationIntent.SpeechError -> handleSpeechError(intent.errorCode, intent.message)
            is ConversationIntent.VideoEnded -> handleVideoEnded()
        }
    }


    private fun handleStartChat() {
        viewModelScope.launch {
            if (!networkMonitor.isConnected()) {
                _event.emit(ConversationEvent.ShowNoInternetDialog)
                return@launch
            }
            val videoResponse = getVideoForKeywordUseCase.getGreetingVideo()
            if (videoResponse != null) {
                updateState {
                    it.copy(
                        currentVideoUri = videoResponse.uri.toUri(),
                        isLooping = getVideoForKeywordUseCase.shouldLoop(videoResponse.videoType),
                        conversationState = videoResponse.conversationState,
                        isListening = false
                    )
                }
            } else { }
        }
    }


    private fun handleSpeechResult(text: String) {
        viewModelScope.launch {
            speechRecognizerHelper.stopListening()

            val videoResponse = getVideoForKeywordUseCase.getVideoForKeyword(text)
            if (videoResponse != null) {
                val isLooping = getVideoForKeywordUseCase.shouldLoop(videoResponse.videoType)
                updateState {
                    it.copy(
                        currentVideoUri = videoResponse.uri.toUri(),
                        isLooping = isLooping,
                        conversationState = videoResponse.conversationState,
                        isListening = false
                    )
                }
            } else {
                handleSpeechError(SpeechRecognizer.ERROR_CLIENT, "Video topilmadi")
            }
        }
    }

    private fun handleSpeechError(errorCode: Int, message: String) {
        viewModelScope.launch {
            _event.emit(ConversationEvent.ShowError(message))
            val videoResponse = getVideoForKeywordUseCase.getFallbackVideo()
            if (videoResponse != null) {
                updateState {
                    it.copy(
                        currentVideoUri = videoResponse.uri.toUri(),
                        isLooping = getVideoForKeywordUseCase.shouldLoop(videoResponse.videoType),
                        conversationState = videoResponse.conversationState,
                        isListening = false
                    )
                }
            }
        }
    }


    private fun handleVideoEnded() {
        viewModelScope.launch {
            val currentState = _state.value.conversationState

            when (currentState) {
                ConversationState.GREETING -> {
                    startListening()
                }
                ConversationState.RESPONDING -> {
                    startListening()
                }
                ConversationState.ERROR -> {
                    startListening()
                }
                ConversationState.GOODBYE -> {
                    loadIdleVideo()
                }
                else -> {}
            }
        }
    }

    private fun startListening() {
        viewModelScope.launch {
            if (!networkMonitor.isConnected()) {
                _event.emit(ConversationEvent.ShowNoInternetDialog)
                return@launch
            }
            val videoResponse = getVideoForKeywordUseCase.getListeningVideo()
            if (videoResponse != null) {
                updateState {
                    it.copy(
                        currentVideoUri = videoResponse.uri.toUri(),
                        isLooping = getVideoForKeywordUseCase.shouldLoop(videoResponse.videoType),
                        conversationState = videoResponse.conversationState,
                        isListening = true
                    )
                }

                speechRecognizerHelper.startListening(
                    onResult = { text ->
                        onAction(ConversationIntent.SpeechResult(text))
                    },
                    onError = { code, message ->
                        onAction(ConversationIntent.SpeechError(code, message))
                    },
                    onListeningChanged = { isListening ->
                        updateState { it.copy(isListening = isListening) }
                    }
                )

                _event.emit(ConversationEvent.StartListening)
            }
        }
    }

    private fun loadIdleVideo() {
        viewModelScope.launch {
            val videoResponse = getVideoForKeywordUseCase.getIdleVideo()
            if (videoResponse != null) {
                updateState {
                    it.copy(
                        currentVideoUri = videoResponse.uri.toUri(),
                        isLooping = getVideoForKeywordUseCase.shouldLoop(videoResponse.videoType),
                        conversationState = videoResponse.conversationState,
                        isListening = false
                    )
                }
            }
        }
    }


    private fun updateState(update: (ConversationUiState) -> ConversationUiState) {
        _state.update(update)
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizerHelper.release()
    }
}

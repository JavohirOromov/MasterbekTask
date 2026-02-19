e# MasterbekTask - Clean Architecture + MVI

## 📁 Loyiha Strukturasi

```
app/src/main/java/com/javohir/masterbektask/
│
├── 📦 data/                          # Data Layer
│   ├── local/
│   │   └── VideoAssetProvider.kt     # Video assetlarni boshqarish
│   ├── repository/
│   │   └── ConversationRepositoryImpl.kt  # Repository implementatsiyasi
│   └── model/
│       └── VideoType.kt              # Data model (Video turlari)
│
├── 📦 domain/                        # Domain Layer (Business Logic)
│   ├── model/
│   │   ├── ConversationState.kt     # Conversation holatlari enum
│   │   └── VideoResponse.kt         # Video javob modeli
│   ├── usecase/
│   │   ├── DetectKeywordUseCase.kt  # Kalit so'zni aniqlash
│   │   └── GetVideoForKeywordUseCase.kt  # Kalit so'zga mos video
│   └── repository/
│       └── IConversationRepository.kt  # Repository interface
│
├── 📦 presentation/                  # Presentation Layer (MVI)
│   ├── conversation/
│   │   ├── ConversationScreen.kt    # View (Compose UI)
│   │   ├── ConversationViewModel.kt  # ViewModel (MVI logic)
│   │   ├── ConversationUiState.kt   # MVI State
│   │   ├── ConversationIntent.kt    # MVI Intent (User actions)
│   │   └── ConversationEffect.kt    # MVI Effect (Side effects)
│   └── components/
│       ├── VideoPlayerView.kt        # ExoPlayer wrapper component
│       ├── SpeechIndicator.kt       # Mikrofon vizual ko'rsatkich
│       └── StartChatButton.kt       # Start Chat button
│
├── 📦 util/                          # Utilities
│   ├── SpeechRecognizerHelper.kt     # SpeechRecognizer wrapper
│   └── VideoPlayerHelper.kt          # ExoPlayer helper functions
│
└── 📦 di/                            # Dependency Injection (Hilt/Koin)
    └── AppModule.kt                  # DI modullari

app/src/main/res/
├── raw/                              # Video fayllar
│   ├── idle.mp4
│   ├── greeting.mp4
│   ├── listening.mp4
│   ├── weather.mp4
│   ├── general_response.mp4
│   ├── goodbye.mp4
│   └── fallback.mp4
```

## 🏗️ Clean Architecture Qatlamlari

### 1. **Domain Layer** (Business Logic)
- **Model**: ConversationState, VideoResponse
- **UseCase**: DetectKeywordUseCase, GetVideoForKeywordUseCase
- **Repository Interface**: IConversationRepository

**Xususiyatlar:**
- Android framework'ga bog'liq emas
- Pure Kotlin
- Business logic va qoidalar

### 2. **Data Layer** (Data Management)
- **Repository Implementation**: ConversationRepositoryImpl
- **Data Sources**: VideoAssetProvider (local assets)
- **Data Models**: VideoType

**Xususiyatlar:**
- Domain layer interface'larini implement qiladi
- Data source'larni boshqaradi
- Android framework bilan ishlaydi

### 3. **Presentation Layer** (MVI Pattern)
- **View**: Compose UI (ConversationScreen) - **Bitta ekran!**
- **ViewModel**: ConversationViewModel
- **State**: ConversationUiState
- **Intent**: ConversationIntent
- **Effect**: ConversationEffect

**⚠️ Muhim:** Loyiha **bitta ekrandan** iborat - ConversationScreen. Barcha holatlar (Idle, Greeting, Listening, Responding, Goodbye) bir xil ekranda o'zgaradi, faqat UI holati va ko'rsatiladigan kontent o'zgaradi.

**MVI Flow:**
```
User Action → Intent → ViewModel → State Change → UI Update
                ↓
            Side Effect (Speech Recognition, Video Playback)
```

**Ekran holatlari:**
- **Idle**: Video player (idle loop) + "Start Chat" button
- **Greeting**: Video player (greeting video)
- **Listening**: Video player (listening loop) + mikrofon indicator
- **Responding**: Video player (response video)
- **Goodbye**: Video player (goodbye video) → keyin Idle'ga qaytadi

## 🔄 MVI Pattern

### State (ConversationUiState)
```kotlin
data class ConversationUiState(
    val conversationState: ConversationState,
    val currentVideoUri: Uri?,
    val isListening: Boolean,
    val error: String?,
    val isLoading: Boolean
)
```

### Intent (User Actions)
```kotlin
sealed class ConversationIntent {
    object StartChat : ConversationIntent()
    object StopChat : ConversationIntent()
    data class SpeechResult(val text: String) : ConversationIntent()
    object SpeechError : ConversationIntent()
    object VideoEnded : ConversationIntent()
}
```

### Effect (Side Effects)
```kotlin
sealed class ConversationEffect {
    object StartListening : ConversationEffect()
    object StopListening : ConversationEffect()
    data class PlayVideo(val uri: Uri) : ConversationEffect()
    object ShowError : ConversationEffect()
}
```

## 🔄 Data Flow

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│  ┌──────────────┐      ┌──────────────┐                 │
│  │     View     │─────▶│  ViewModel   │                 │
│  │  (Compose)   │◀─────│    (MVI)     │                 │
│  └──────────────┘      └──────────────┘                 │
│                              │                           │
└──────────────────────────────┼───────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────┐
│                      Domain Layer                       │
│  ┌──────────────┐      ┌──────────────┐                │
│  │   UseCase    │─────▶│  Repository  │                │
│  │              │      │  Interface   │                │
│  └──────────────┘      └──────────────┘                │
└──────────────────────────────┼───────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────┐
│                       Data Layer                        │
│  ┌──────────────┐      ┌──────────────┐                │
│  │  Repository  │─────▶│  Data Source │                │
│  │ Implementation      │  (Assets)    │                │
│  └──────────────┘      └──────────────┘                │
└─────────────────────────────────────────────────────────┘
```

## 📋 Asosiy Komponentlar

### 1. **ConversationState** (Domain)
- Idle, Greeting, Listening, Responding, Goodbye, Error

### 2. **VideoPlayerView** (Presentation)
- ExoPlayer wrapper
- Seamless transitions uchun preloading
- State management

### 3. **SpeechRecognizerHelper** (Util)
- Android SpeechRecognizer wrapper
- Lifecycle aware
- Error handling

### 4. **ConversationViewModel** (Presentation)
- MVI pattern implementatsiyasi
- State management
- Intent processing
- Effect handling

## 🎯 Dependency Flow

```
Presentation → Domain ← Data
     │           │
     └───────────┘
   (no direct dependency)
```

**Qoida:** Presentation layer faqat Domain laye'ga bog'liq, Data layer'ga emas!

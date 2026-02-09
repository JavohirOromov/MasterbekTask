# MasterbekTask

Video suhbat ilovasi — ovoz orqali kalit so‘z aniqlanadi va mos video o‘ynatiladi.

## Xususiyatlar

- **Kalit so‘z bo‘yicha video** — salom, xayr, ob-havo, umumiy javob
- **Ovozni tanlash** — Android SpeechRecognizer (en-US)
- **Ikki ExoPlayer** — videolar o‘rtasida uzilishsiz almashish
- **Jetpack Compose** — bitta ekran, MVI-style (Intent → State)
- **Clean Architecture** — domain, data, presentation, DI (Hilt)

## Texnologiyalar

- **Kotlin**, **Jetpack Compose**, **Material 3**
- **ExoPlayer (Media3)** — video
- **SpeechRecognizer API** — ovozni matnga
- **Hilt** — dependency injection
- **Coroutines & StateFlow** — state boshqaruvi

## O‘rnatish va ishga tushirish

1. Repozitoriyani klonlash:ash

https://github.com/JavohirOromov/MasterbekTask.git


Setup & run (O‘rnatish va ishga tushirish)
Reponi klonlang → Android Studio da oching → Gradle sync → Run. Start/Chat bosib, mikrofon ruxsatini bering va so‘z ayting.
Architecture (Arxitektura)

Clean Architecture (domain / data / presentation) + MVI (Intent → State). Biznes mantiq alohida, UI faqat state ni ko‘rsatadi, test va o‘zgartirish oson.
Video playback (Videoni uzilishsiz o‘ynatish)
Ikki ExoPlayer (A va B). Yangi video orqa playerda yuklanadi, tayyor bo‘lgach front almashtiriladi — uzilishsiz ko‘rinadi. Loop faqat yangi video uchun o‘rnatiladi.

Speech va kalit so‘z
Android SpeechRecognizer (en-US). Matn kelgach DetectKeywordUseCase: goodbye → greeting → weather (weather, rain, sunny, …) → qolgani GENERAL_RESPONSE. Substring orqali tekshiriladi.

Nima qilindi / stretch
Qilindi: to‘liq tsikl (idle → greeting → listening → javob/fallback → goodbye), barcha video turlari, ikki player, fallback ("I didn’t catch that"). Stretch: silence prompt (PROMPT) va ko‘p tillar hali ulangan emas.

Muammolar va yechimlar
1) "today"/"hot"/"cold" WEATHER da edi → olib tashlandi. 2) LaunchedEffect(isLooping) eski videoni tugatib, state ustiga yozardi → blok olib tashlandi. 3) GetVideoForKeywordUseCase.kt da noto‘g‘ri klass edi → to‘g‘ri use case yozildi.
Faqat en-US, substring matching, videolar faqat raw. Kelajakda: , so‘z bo‘yicha matching, ko‘p tillar, backend/NLU, testlar.

# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

AI Gochi (Echo) is an Android application built as an AI Tamagochi that responds to user emotions. It's an emotional companion app developed in Kotlin using Jetpack Compose. The app detects user emotions through text input or button presses and responds with appropriate visual, audio, and haptic feedback.

## Essential Commands

### Building and Development
```bash
# Build the project
./gradlew build

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean build artifacts
./gradlew clean

# Install on connected device
./gradlew installDebug
```

### Testing and Verification
```bash
# Run all tests
./gradlew test

# Run unit tests only
./gradlew testDebugUnitTest

# Run instrumentation tests
./gradlew connectedAndroidTest

# Run lint analysis
./gradlew lint

# Fix lint issues automatically
./gradlew lintFix

# Run all checks (tests + lint)
./gradlew check
```

### Project Compilation
```bash
# Compile debug sources
./gradlew compileDebugSources

# Compile release sources
./gradlew compileReleaseSources
```

## Architecture Overview

### Core Architecture Pattern
The app follows MVVM (Model-View-ViewModel) architecture with reactive programming using StateFlow and LiveData.

### Key Components

**Data Layer (`com.hackathon.echo.data`)**:
- `PetState.kt` - Core emotion states and pet appearance data model
- `ResponseBank.kt` - Pre-defined responses for each emotion type
- `EchoPreferences.kt` - SharedPreferences wrapper for persistent storage
- `MemorySystem.kt` - Personalized memory and greeting system
- `DemoScenario.kt` - Demo presentation scenarios and scripted phrases

**ViewModel Layer (`com.hackathon.echo.viewmodel`)**:
- `EchoViewModel.kt` - Main business logic controller with StateFlow management
- Handles emotion detection from text using keyword-based analysis
- Manages interaction history and persistent storage
- Controls sound/vibration feedback through SoundManager

**UI Layer (`com.hackathon.echo.ui`)**:
- `MainActivity.kt` - Single activity with navigation setup
- `screens/` - SplashScreen and MainScreen composables
- `components/` - Reusable UI components (PetAvatar, RoomBackground, ChatBubble, EmotionButtons, EmotionAnalytics)
- `theme/` - Material Design theming (Color.kt, Theme.kt, Type.kt)

**Utils Layer (`com.hackathon.echo.utils`)**:
- `AnimationUtils.kt` - Animation constants and utilities for smooth transitions
- `SoundManager.kt` - Audio and haptic feedback management using MediaPlayer and Vibrator APIs

### Emotion System
The app recognizes 4 emotional states:
- JOY (golden #FFD700) - warm lighting, blooming plant, sunny weather
- SADNESS (royal blue #4169E1) - cold lighting, wilting plant, cloudy weather
- THOUGHTFUL (medium purple #9370DB) - soft lighting, normal plant, clear weather
- NEUTRAL (gray #CCCCCC) - balanced lighting, normal plant, clear weather

### State Management
- StateFlow for reactive UI updates
- SharedPreferences for persistence (emotion history, friendship days, achievements)
- LiveData for interaction history and achievements
- Coroutine-based async operations

### Audio System
Sound files located in `app/src/main/res/raw/`:
- `joy_chime.mp3`, `sad_ambient.mp3`, `thoughtful_meditation.mp3`, `calm_nature.mp3`
- `message_send.mp3`, `message_receive.mp3`
- Vibration patterns for haptic feedback (requires VIBRATE permission)
- Managed through SoundManager singleton with proper lifecycle handling

## Development Guidelines

### Styling Patterns
- Uses Jetpack Compose with Material 3 design system
- Animation durations: fast (400ms), default (800ms), slow (1200ms)
- Color scheme based on emotion states with smooth transitions
- Canvas-based particle effects for visual feedback

### Data Persistence
- SharedPreferences with kotlinx.serialization for JSON storage
- Emotion history limited to 20 entries for memory efficiency
- Achievement system with automatic unlocking logic
- Usage statistics tracking with friendship day counter

### Testing Approach
- Unit tests in `app/src/test/java/com/hackathon/echo/ExampleUnitTest.kt`
- Instrumentation tests in `app/src/androidTest/java/com/hackathon/echo/ExampleInstrumentedTest.kt`
- Demo scenarios in `DemoScenario.kt` for presentation testing

### Dependencies Management
- Uses Gradle Version Catalog (`gradle/libs.versions.toml`)
- Key dependencies: Compose BOM, Lottie Compose, Navigation Compose, Kotlinx Serialization
- Minimum SDK: 24 (Android 7.0), Target SDK: 36

## Important Implementation Details

### ViewModel Context Dependency
EchoViewModel requires Android Context for SharedPreferences and SoundManager. Use EchoViewModelFactory in Compose:
```kotlin
val viewModel: EchoViewModel = viewModel(factory = EchoViewModelFactory(LocalContext.current))
```

### Emotion Detection Algorithm
Text-based emotion recognition uses keyword matching with scoring system. Keywords are in Russian language. Fallback to NEUTRAL state when no emotion is detected.

### Resource Management
- MediaPlayer instances managed by SoundManager with proper lifecycle
- Particle effects optimized with limited particle counts
- Memory-conscious interaction history (max 50 entries)

### Demo Presentation System
The app includes a complete demo scenario system for hackathon presentations:
- Cyclic demo phrases for each emotion type in Russian
- Pre-scripted emotional responses for quick testing
- Demo buttons in ChatInterface for rapid emotion demonstration
- Accessible through `DemoScriptedPhrases` object

## Core Business Logic

### Emotion Detection Algorithm
The app uses Russian keyword-based emotion detection in `EchoViewModel.detectEmotionFromText()`:
- **Joy keywords**: радость, счастлив, отлично, здорово, супер, прекрасно, восторг, ура, победа, успех, достижение, поздравь, праздник
- **Sadness keywords**: грустно, печально, расстроен, переживаю, тревога, проблема, болит, тяжело, плохо, устал, депрессия, одиноко, страшно
- **Thoughtful keywords**: думаю, размышляю, интересно, будущее, философия, смысл, вопрос, почему, задаюсь, мысли, анализ, понимание, мудрость
- Scoring system counts keyword matches, highest score wins
- Fallback to NEUTRAL if no keywords match

### Pet Statistics System
Each emotion has a stat value (0-100) tracked in `PetStats`:
- Stats increase by +10 when corresponding emotion is triggered
- Stats can be manually decreased by -5 if needed
- All changes are automatically saved to SharedPreferences
- Visual feedback through `StatsChangeModal` component

### Memory and Personalization
`MemorySystem` provides context-aware responses:
- Personalized greetings based on usage patterns and time since last interaction
- Reflective questions after emotion changes
- Special responses for repeated emotions or detailed messages
- Achievement tracking with milestone celebrations
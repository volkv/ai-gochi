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
The app recognizes 5 emotional states:
- JOY (yellow) - bouncing animations, golden sparkles
- SADNESS (blue) - shrinking animations, tear drops
- THOUGHTFUL (purple) - meditation pose, mystical aura
- CALM (green) - breathing animation, minimal effects
- NEUTRAL (gray) - default state

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
- 6-step presentation flow (330 seconds total)
- Pre-scripted phrases for each emotion type
- Timing guidance and fallback plans in `DemoScenario.kt`
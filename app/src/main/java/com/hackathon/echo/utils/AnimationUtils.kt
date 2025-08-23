package com.hackathon.echo.utils

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.getValue
import com.hackathon.echo.data.EmotionType
import com.hackathon.echo.data.PetState

object AnimationUtils {
    const val DEFAULT_TRANSITION_DURATION = 800
    const val FAST_TRANSITION_DURATION = 400
    const val SLOW_TRANSITION_DURATION = 1200
    
    val ElegantEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)
    val BouncyEasing = EaseOutBack
    val SmoothEasing = EaseInOut
    
    val springyTransition = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
    
    val smoothTransition = tween<Float>(
        durationMillis = DEFAULT_TRANSITION_DURATION,
        easing = ElegantEasing
    )
    
    val fastTransition = tween<Float>(
        durationMillis = FAST_TRANSITION_DURATION,
        easing = SmoothEasing
    )
    
    val slowTransition = tween<Float>(
        durationMillis = SLOW_TRANSITION_DURATION,
        easing = ElegantEasing
    )
    
    val colorTransition = tween<Color>(
        durationMillis = DEFAULT_TRANSITION_DURATION,
        easing = ElegantEasing
    )
}

@Composable
fun animatedPetColor(
    targetState: PetState,
    animationSpec: AnimationSpec<Color> = AnimationUtils.colorTransition
): State<Color> {
    return animateColorAsState(
        targetValue = targetState.color,
        animationSpec = animationSpec,
        label = "PetColorAnimation"
    )
}

@Composable
fun animatedStateTransition(
    targetEmotion: EmotionType,
    animationSpec: AnimationSpec<Float> = AnimationUtils.smoothTransition
): StateTransitionValues {
    val targetScale by animateFloatAsState(
        targetValue = when (targetEmotion) {
            EmotionType.JOY -> 1.1f
            EmotionType.SADNESS -> 0.9f
            EmotionType.THOUGHTFUL -> 1.0f

            EmotionType.NEUTRAL -> 1.0f
        },
        animationSpec = animationSpec,
        label = "StateScaleAnimation"
    )
    
    val targetRotation by animateFloatAsState(
        targetValue = when (targetEmotion) {
            EmotionType.THOUGHTFUL -> 360f
            else -> 0f
        },
        animationSpec = tween(
            durationMillis = AnimationUtils.SLOW_TRANSITION_DURATION,
            easing = AnimationUtils.ElegantEasing
        ),
        label = "StateRotationAnimation"
    )
    
    val targetAlpha by animateFloatAsState(
        targetValue = when (targetEmotion) {
            EmotionType.SADNESS -> 0.85f
            else -> 1.0f
        },
        animationSpec = animationSpec,
        label = "StateAlphaAnimation"
    )
    
    return StateTransitionValues(
        scale = targetScale,
        rotation = targetRotation,
        alpha = targetAlpha
    )
}

data class StateTransitionValues(
    val scale: Float,
    val rotation: Float,
    val alpha: Float
)

@Composable
fun animatedRoomLighting(
    targetState: PetState,
    animationSpec: AnimationSpec<Float> = AnimationUtils.smoothTransition
): RoomAnimationValues {
    val lightingIntensity by animateFloatAsState(
        targetValue = when (targetState.roomLighting) {
            com.hackathon.echo.data.RoomLighting.WARM -> 1.0f
            com.hackathon.echo.data.RoomLighting.COLD -> 0.6f
            com.hackathon.echo.data.RoomLighting.SOFT -> 0.8f
            com.hackathon.echo.data.RoomLighting.BALANCED -> 0.9f
        },
        animationSpec = animationSpec,
        label = "LightingIntensityAnimation"
    )
    
    val windowBrightness by animateFloatAsState(
        targetValue = when (targetState.weatherState) {
            com.hackathon.echo.data.WeatherState.SUNNY -> 1.0f
            com.hackathon.echo.data.WeatherState.CLOUDY -> 0.4f
            com.hackathon.echo.data.WeatherState.CLEAR -> 0.8f
        },
        animationSpec = animationSpec,
        label = "WindowBrightnessAnimation"
    )
    
    val plantScale by animateFloatAsState(
        targetValue = when (targetState.plantState) {
            com.hackathon.echo.data.PlantState.BLOOMING -> 1.2f
            com.hackathon.echo.data.PlantState.WILTING -> 0.7f
            com.hackathon.echo.data.PlantState.NORMAL -> 1.0f
        },
        animationSpec = tween(
            durationMillis = AnimationUtils.SLOW_TRANSITION_DURATION,
            easing = AnimationUtils.ElegantEasing
        ),
        label = "PlantScaleAnimation"
    )
    
    val candleAlpha by animateFloatAsState(
        targetValue = if (targetState.emotion == EmotionType.THOUGHTFUL) 1.0f else 0.0f,
        animationSpec = animationSpec,
        label = "CandleAlphaAnimation"
    )
    
    return RoomAnimationValues(
        lightingIntensity = lightingIntensity,
        windowBrightness = windowBrightness,
        plantScale = plantScale,
        candleAlpha = candleAlpha
    )
}

data class RoomAnimationValues(
    val lightingIntensity: Float,
    val windowBrightness: Float,
    val plantScale: Float,
    val candleAlpha: Float
)

@Composable
fun animatedParticleEffects(
    targetEmotion: EmotionType,
    time: Float
): ParticleAnimationValues {
    val particleDensity by animateFloatAsState(
        targetValue = when (targetEmotion) {
            EmotionType.JOY -> 1.0f
            EmotionType.SADNESS -> 0.4f
            EmotionType.THOUGHTFUL -> 0.7f

            EmotionType.NEUTRAL -> 0.0f
        },
        animationSpec = AnimationUtils.smoothTransition,
        label = "ParticleDensityAnimation"
    )
    
    val particleSpeed by animateFloatAsState(
        targetValue = when (targetEmotion) {
            EmotionType.JOY -> 3.0f
            EmotionType.SADNESS -> 1.0f
            EmotionType.THOUGHTFUL -> 1.5f

            EmotionType.NEUTRAL -> 0.0f
        },
        animationSpec = AnimationUtils.fastTransition,
        label = "ParticleSpeedAnimation"
    )
    
    val particleSize by animateFloatAsState(
        targetValue = when (targetEmotion) {
            EmotionType.JOY -> 1.2f
            EmotionType.SADNESS -> 0.8f
            EmotionType.THOUGHTFUL -> 1.1f

            EmotionType.NEUTRAL -> 0.0f
        },
        animationSpec = AnimationUtils.smoothTransition,
        label = "ParticleSizeAnimation"
    )
    
    return ParticleAnimationValues(
        density = particleDensity,
        speed = particleSpeed,
        size = particleSize,
        timeOffset = time
    )
}

data class ParticleAnimationValues(
    val density: Float,
    val speed: Float,
    val size: Float,
    val timeOffset: Float
)

@Composable
fun rememberMorphingTransition(
    currentState: PetState,
    targetState: PetState
): MorphTransitionState {
    val progress by animateFloatAsState(
        targetValue = if (currentState == targetState) 1f else 0f,
        animationSpec = AnimationUtils.smoothTransition,
        label = "MorphTransition"
    )
    
    return remember(currentState, targetState, progress) {
        derivedStateOf {
            MorphTransitionState(
                progress = progress,
                fromState = currentState,
                toState = targetState,
                isTransitioning = progress < 1f && progress > 0f
            )
        }
    }.value
}

data class MorphTransitionState(
    val progress: Float,
    val fromState: PetState,
    val toState: PetState,
    val isTransitioning: Boolean
)
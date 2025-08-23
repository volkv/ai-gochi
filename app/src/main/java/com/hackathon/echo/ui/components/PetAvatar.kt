package com.hackathon.echo.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hackathon.echo.data.EmotionType
import com.hackathon.echo.data.PetState
import com.hackathon.echo.utils.animatedPetColor
import com.hackathon.echo.utils.animatedParticleEffects
import com.hackathon.echo.utils.animatedStateTransition
import com.hackathon.echo.utils.AnimationUtils
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PetAvatar(
    petState: PetState,
    size: Dp = 120.dp,
    modifier: Modifier = Modifier
) {
    val animatedColor by animatedPetColor(petState, AnimationUtils.colorTransition)
    val stateTransition = animatedStateTransition(petState.emotion, AnimationUtils.smoothTransition)
    val particleEffects = animatedParticleEffects(petState.emotion, stateTransition.rotation)
    
    val animatedOffset = remember { Animatable(0f) }
    val animatedScale = remember { Animatable(1f) }
    val animatedRotation = remember { Animatable(0f) }
    
    LaunchedEffect(petState.emotion) {
        when (petState.emotion) {
            EmotionType.JOY -> {
                animatedScale.animateTo(
                    targetValue = 1f,
                    animationSpec = AnimationUtils.fastTransition
                )
                animatedRotation.snapTo(0f)
                animatedOffset.animateTo(
                    targetValue = -15f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, easing = AnimationUtils.BouncyEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
            EmotionType.SADNESS -> {
                animatedOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = AnimationUtils.smoothTransition
                )
                animatedRotation.snapTo(0f)
                animatedScale.animateTo(
                    targetValue = 0.8f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = AnimationUtils.ElegantEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
            EmotionType.THOUGHTFUL -> {
                animatedScale.animateTo(
                    targetValue = 1f,
                    animationSpec = AnimationUtils.smoothTransition
                )
                animatedOffset.snapTo(0f)
                animatedRotation.animateTo(
                    targetValue = 5f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3000, easing = AnimationUtils.ElegantEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
            EmotionType.CALM -> {
                animatedOffset.snapTo(0f)
                animatedRotation.snapTo(0f)
                animatedScale.animateTo(
                    targetValue = 1.05f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = AnimationUtils.SmoothEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
            else -> {
                animatedOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = AnimationUtils.smoothTransition
                )
                animatedScale.animateTo(
                    targetValue = 1f,
                    animationSpec = AnimationUtils.smoothTransition
                )
                animatedRotation.animateTo(
                    targetValue = 0f,
                    animationSpec = AnimationUtils.smoothTransition
                )
            }
        }
    }
    
    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer(
                scaleX = stateTransition.scale,
                scaleY = stateTransition.scale,
                alpha = stateTransition.alpha
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
        ) {
            scale(animatedScale.value) {
                rotate(animatedRotation.value) {
                    drawPet(
                        petState = petState.copy(color = animatedColor),
                        size = this.size.width,
                        animationOffset = animatedOffset.value,
                        rotation = animatedRotation.value
                    )
                }
            }
            
            drawEnhancedParticleEffects(
                emotion = petState.emotion,
                size = this.size.width,
                particleValues = particleEffects
            )
        }
    }
}

private fun DrawScope.drawPet(
    petState: PetState,
    size: Float,
    animationOffset: Float,
    rotation: Float = 0f
) {
    val center = Offset(size / 2, size / 2)
    val radius = size / 2.5f
    
    drawCircle(
        color = petState.color,
        radius = radius,
        center = center.copy(y = center.y + animationOffset)
    )
    
    val eyeOffset = radius * 0.3f
    val eyeRadius = radius * 0.15f
    val eyeY = center.y - radius * 0.2f + animationOffset
    
    when (petState.emotion) {
        EmotionType.JOY -> {
            drawCircle(
                color = Color.Black,
                radius = eyeRadius,
                center = Offset(center.x - eyeOffset, eyeY)
            )
            drawCircle(
                color = Color.Black,
                radius = eyeRadius,
                center = Offset(center.x + eyeOffset, eyeY)
            )
            drawCircle(
                color = Color.White,
                radius = eyeRadius * 0.4f,
                center = Offset(center.x - eyeOffset + eyeRadius * 0.3f, eyeY - eyeRadius * 0.3f)
            )
            drawCircle(
                color = Color.White,
                radius = eyeRadius * 0.4f,
                center = Offset(center.x + eyeOffset + eyeRadius * 0.3f, eyeY - eyeRadius * 0.3f)
            )
        }
        EmotionType.SADNESS -> {
            drawCircle(
                color = Color.Black,
                radius = eyeRadius * 0.8f,
                center = Offset(center.x - eyeOffset, eyeY + radius * 0.1f)
            )
            drawCircle(
                color = Color.Black,
                radius = eyeRadius * 0.8f,
                center = Offset(center.x + eyeOffset, eyeY + radius * 0.1f)
            )
            drawCircle(
                color = Color.Blue.copy(alpha = 0.7f),
                radius = 3f,
                center = Offset(center.x - eyeOffset * 0.5f, eyeY + radius * 0.4f)
            )
        }
        EmotionType.THOUGHTFUL -> {
            drawLine(
                color = Color.Black,
                start = Offset(center.x - eyeOffset - eyeRadius, eyeY),
                end = Offset(center.x - eyeOffset + eyeRadius, eyeY),
                strokeWidth = 4f
            )
            drawLine(
                color = Color.Black,
                start = Offset(center.x + eyeOffset - eyeRadius, eyeY),
                end = Offset(center.x + eyeOffset + eyeRadius, eyeY),
                strokeWidth = 4f
            )
        }
        EmotionType.CALM -> {
            drawCircle(
                color = Color.Black,
                radius = eyeRadius * 0.7f,
                center = Offset(center.x - eyeOffset, eyeY)
            )
            drawCircle(
                color = Color.Black,
                radius = eyeRadius * 0.7f,
                center = Offset(center.x + eyeOffset, eyeY)
            )
        }
        else -> {
            drawCircle(
                color = Color.Black,
                radius = eyeRadius,
                center = Offset(center.x - eyeOffset, eyeY)
            )
            drawCircle(
                color = Color.Black,
                radius = eyeRadius,
                center = Offset(center.x + eyeOffset, eyeY)
            )
        }
    }
}

private fun DrawScope.drawEnhancedParticleEffects(
    emotion: EmotionType,
    size: Float,
    particleValues: com.hackathon.echo.utils.ParticleAnimationValues
) {
    val center = Offset(size / 2, size / 2)
    val radius = size / 2
    val time = particleValues.timeOffset
    
    when (emotion) {
        EmotionType.JOY -> {
            val particleCount = (8 * particleValues.density).toInt()
            repeat(particleCount) { i ->
                val angle = (i * 45f + time * 50f * particleValues.speed) * Math.PI / 180f
                val particleRadius = radius + 25f + sin(time * 3f + i) * 15f
                val sparklePos = Offset(
                    center.x + cos(angle).toFloat() * particleRadius,
                    center.y + sin(angle).toFloat() * particleRadius
                )
                drawCircle(
                    color = Color(0xFFFFD700).copy(alpha = 0.9f * particleValues.density),
                    radius = (4f + sin(time * 4f + i).toFloat() * 2f) * particleValues.size,
                    center = sparklePos
                )
            }
        }
        EmotionType.SADNESS -> {
            val particleCount = (3 * particleValues.density).toInt().coerceAtLeast(1)
            repeat(particleCount) { i ->
                val dropX = center.x - radius * 0.3f + i * radius * 0.3f
                val dropY = center.y + radius * 0.6f + (time * particleValues.speed + i * 2f) % 40f
                drawCircle(
                    color = Color(0xFF4169E1).copy(alpha = 0.7f * particleValues.density),
                    radius = 3f * particleValues.size,
                    center = Offset(dropX, dropY)
                )
            }
        }
        EmotionType.THOUGHTFUL -> {
            val particleCount = (5 * particleValues.density).toInt()
            repeat(particleCount) { i ->
                val angle = (i * 72f + time * 20f * particleValues.speed) * Math.PI / 180f
                val auraRadius = radius + 18f + sin(time * 2f + i) * 12f
                val auraPos = Offset(
                    center.x + cos(angle).toFloat() * auraRadius,
                    center.y + sin(angle).toFloat() * auraRadius
                )
                drawCircle(
                    color = Color(0xFF9370DB).copy(
                        alpha = (0.4f + sin(time * 3f + i) * 0.3f) * particleValues.density
                    ),
                    radius = (6f + sin(time * 2f + i) * 2f) * particleValues.size,
                    center = auraPos
                )
            }
        }
        EmotionType.CALM -> {
            val particleCount = (2 * particleValues.density).toInt()
            repeat(particleCount) { i ->
                val angle = (i * 180f + time * 10f * particleValues.speed) * Math.PI / 180f
                val particleRadius = radius + 30f
                val calmPos = Offset(
                    center.x + cos(angle).toFloat() * particleRadius,
                    center.y + sin(angle).toFloat() * particleRadius
                )
                drawCircle(
                    color = Color(0xFF32CD32).copy(alpha = 0.2f * particleValues.density),
                    radius = 2f * particleValues.size,
                    center = calmPos
                )
            }
        }
        else -> { }
    }
}
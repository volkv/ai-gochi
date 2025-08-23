package com.hackathon.echo.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.sin
import com.hackathon.echo.data.EmotionType
import com.hackathon.echo.data.PetState
import com.hackathon.echo.R

@Composable
fun PetAvatar(
    petState: PetState,
    size: Dp = 180.dp,
    modifier: Modifier = Modifier
) {
    val imageResource = when (petState.emotion) {
        EmotionType.JOY -> R.drawable.h
        EmotionType.SADNESS -> R.drawable.s
        EmotionType.THOUGHTFUL -> R.drawable.m
        EmotionType.NEUTRAL -> R.drawable.n
    }
    
    // Анимация плавания
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Restart
        ),
        label = "floating_offset"
    )
    
    // Вычисляем вертикальное смещение на основе синуса
    val yOffset = (sin(floatOffset) * 8f).dp
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = "Pet avatar in ${petState.emotion.name} state",
            modifier = Modifier
                .size(size)
                .offset(y = yOffset),
            contentScale = ContentScale.Fit
        )
    }
}


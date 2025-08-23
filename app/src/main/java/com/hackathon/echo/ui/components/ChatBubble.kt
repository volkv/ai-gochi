package com.hackathon.echo.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hackathon.echo.utils.AnimationUtils
import kotlinx.coroutines.delay

@Composable
fun ChatBubble(
    message: String,
    isVisible: Boolean = true,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var displayedText by remember { mutableStateOf("") }
    var charIndex by remember { mutableIntStateOf(0) }
    val alpha = remember { Animatable(0f) }
    val translateY = remember { Animatable(50f) }
    
    LaunchedEffect(isVisible) {
        if (isVisible) {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = AnimationUtils.FAST_TRANSITION_DURATION,
                    easing = AnimationUtils.ElegantEasing
                )
            )
            translateY.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = AnimationUtils.FAST_TRANSITION_DURATION,
                    easing = AnimationUtils.BouncyEasing
                )
            )
            
            while (charIndex < message.length) {
                displayedText = message.take(charIndex + 1)
                charIndex++
                delay(25) // Slightly faster typewriter effect
            }
            
            delay(5000)
            
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = AnimationUtils.FAST_TRANSITION_DURATION,
                    easing = AnimationUtils.ElegantEasing
                )
            )
            translateY.animateTo(
                targetValue = -20f,
                animationSpec = tween(
                    durationMillis = AnimationUtils.FAST_TRANSITION_DURATION,
                    easing = AnimationUtils.SmoothEasing
                )
            )
            onDismiss()
        } else {
            charIndex = 0
            displayedText = ""
            alpha.snapTo(0f)
            translateY.snapTo(50f)
        }
    }
    
    if (isVisible) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.8f)
                .graphicsLayer(
                    alpha = alpha.value,
                    translationY = translateY.value
                )
                .background(
                    color = Color(0xFF3B82F6).copy(alpha = 0.9f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayedText,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )
        }
    }
}
package com.hackathon.echo.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hackathon.echo.ui.theme.Calm
import com.hackathon.echo.ui.theme.Joy
import com.hackathon.echo.ui.theme.Sadness
import com.hackathon.echo.ui.theme.Thoughtful

data class EmotionButtonData(
    val emotion: EmotionType,
    val text: String,
    val emoji: String,
    val color: Color
)

@Composable
fun EmotionButtons(
    onEmotionSelected: (EmotionType) -> Unit,
    modifier: Modifier = Modifier
) {
    val emotions = listOf(
        EmotionButtonData(
            emotion = EmotionType.JOY,
            text = "Поделиться радостью",
            emoji = "🌟",
            color = Joy
        ),
        EmotionButtonData(
            emotion = EmotionType.SADNESS,
            text = "Рассказать о тревоге",
            emoji = "💙",
            color = Sadness
        ),
        EmotionButtonData(
            emotion = EmotionType.THOUGHTFUL,
            text = "Поразмышлять",
            emoji = "🤔",
            color = Thoughtful
        ),
        EmotionButtonData(
            emotion = EmotionType.CALM,
            text = "Побыть в тишине",
            emoji = "🍃",
            color = Calm
        )
    )
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        emotions.chunked(2).forEach { rowEmotions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowEmotions.forEach { emotionData ->
                    EmotionButton(
                        emotionData = emotionData,
                        onClick = { onEmotionSelected(emotionData.emotion) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmotionButton(
    emotionData: EmotionButtonData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "button_scale"
    )
    
    Button(
        onClick = {
            isPressed = true
            onClick()
            isPressed = false
        },
        modifier = modifier
            .scale(scale)
            .size(width = 160.dp, height = 80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = emotionData.color,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emotionData.emoji,
                fontSize = 24.sp
            )
            Text(
                text = emotionData.text,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 12.sp
            )
        }
    }
}
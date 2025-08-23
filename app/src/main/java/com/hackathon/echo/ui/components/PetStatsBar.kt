package com.hackathon.echo.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hackathon.echo.data.EmotionType
import com.hackathon.echo.data.PetStats
import com.hackathon.echo.ui.theme.Joy
import com.hackathon.echo.ui.theme.Sadness
import com.hackathon.echo.ui.theme.Thoughtful
import com.hackathon.echo.ui.theme.Neutral
import com.hackathon.echo.utils.AnimationUtils

data class StatBarData(
    val emotion: EmotionType,
    val name: String,
    val emoji: String,
    val color: Color,
    val value: Int
)

@Composable
fun PetStatsBar(
    petStats: PetStats,
    modifier: Modifier = Modifier
) {
    val statsData = listOf(
        StatBarData(
            emotion = EmotionType.JOY,
            name = "Радость",
            emoji = "😊",
            color = Joy,
            value = petStats.joy
        ),
        StatBarData(
            emotion = EmotionType.SADNESS,
            name = "Грусть",
            emoji = "😢",
            color = Sadness,
            value = petStats.sadness
        ),
        StatBarData(
            emotion = EmotionType.THOUGHTFUL,
            name = "Мысли",
            emoji = "🤔",
            color = Thoughtful,
            value = petStats.thoughtful
        ),
        StatBarData(
            emotion = EmotionType.NEUTRAL,
            name = "Спокойствие",
            emoji = "😐",
            color = Neutral,
            value = petStats.neutral
        )
    )
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Эмоциональное состояние",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            statsData.forEach { statData ->
                StatBar(statData = statData)
            }
        }
    }
}

@Composable
private fun StatBar(
    statData: StatBarData
) {
    val animatedProgress by animateFloatAsState(
        targetValue = statData.value / 100f,
        animationSpec = tween(
            durationMillis = 800,
            easing = AnimationUtils.SmoothEasing
        ),
        label = "stat_progress"
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = statData.emoji,
            fontSize = 16.sp,
            modifier = Modifier.size(20.dp)
        )
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = statData.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${statData.value}%",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = statData.color
                )
            }
            
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                
                drawRoundRect(
                    color = Color.Gray.copy(alpha = 0.3f),
                    topLeft = Offset.Zero,
                    size = Size(canvasWidth, canvasHeight),
                    cornerRadius = CornerRadius(canvasHeight / 2)
                )
                
                drawRoundRect(
                    color = statData.color,
                    topLeft = Offset.Zero,
                    size = Size(canvasWidth * animatedProgress, canvasHeight),
                    cornerRadius = CornerRadius(canvasHeight / 2)
                )
            }
        }
    }
}
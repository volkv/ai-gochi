package com.hackathon.echo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hackathon.echo.data.EmotionType
import kotlin.math.*

data class EmotionAnalytics(
    val emotionCounts: Map<EmotionType, Int>,
    val totalInteractions: Int,
    val friendshipDays: Int,
    val averageEmotionsPerDay: Float,
    val dominantEmotion: EmotionType?,
    val emotionBalance: Float
)

@Composable
fun EmotionAnalyticsCard(
    analytics: EmotionAnalytics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Эмоциональная аналитика",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EmotionPieChart(
                    emotionCounts = analytics.emotionCounts,
                    modifier = Modifier
                        .size(120.dp)
                        .weight(1f)
                )
                
                EmotionLegend(
                    emotionCounts = analytics.emotionCounts,
                    totalInteractions = analytics.totalInteractions,
                    modifier = Modifier.weight(1f)
                )
            }
            
            EmotionBalanceIndicator(
                balance = analytics.emotionBalance,
                modifier = Modifier.fillMaxWidth()
            )
            
            EmotionStatsGrid(
                analytics = analytics,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun EmotionPieChart(
    emotionCounts: Map<EmotionType, Int>,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val total = emotionCounts.values.sum().toFloat()
    
    if (total == 0f) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Нет данных",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
        return
    }
    
    Canvas(modifier = modifier) {
        val canvasSize = size.minDimension
        val radius = canvasSize / 2 * 0.8f
        val center = Offset(size.width / 2, size.height / 2)
        val strokeWidth = with(density) { 12.dp.toPx() }
        
        var startAngle = 0f
        
        emotionCounts.forEach { (emotion, count) ->
            val sweepAngle = (count / total) * 360f
            val emotionColor = getEmotionColor(emotion)
            
            drawArc(
                color = emotionColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth)
            )
            
            startAngle += sweepAngle
        }
        
        drawCircle(
            color = Color.White,
            radius = radius - strokeWidth / 2,
            center = center,
            style = Stroke(width = with(density) { 2.dp.toPx() })
        )
    }
}

@Composable
fun EmotionLegend(
    emotionCounts: Map<EmotionType, Int>,
    totalInteractions: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        emotionCounts.forEach { (emotion, count) ->
            val percentage = if (totalInteractions > 0) {
                (count.toFloat() / totalInteractions * 100).toInt()
            } else 0
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(getEmotionColor(emotion))
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = getEmotionName(emotion),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$count ($percentage%)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
fun EmotionBalanceIndicator(
    balance: Float,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Эмоциональный баланс",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${(balance * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = getBalanceColor(balance)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        EmotionBalanceBar(
            balance = balance,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = getBalanceDescription(balance),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun EmotionBalanceBar(
    balance: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.clip(RoundedCornerShape(4.dp))) {
        val barWidth = size.width
        val barHeight = size.height
        
        drawRect(
            color = Color.Gray.copy(alpha = 0.3f),
            size = Size(barWidth, barHeight)
        )
        
        val filledWidth = barWidth * balance.coerceIn(0f, 1f)
        drawRect(
            color = getBalanceColor(balance),
            size = Size(filledWidth, barHeight)
        )
    }
}

@Composable
fun EmotionStatsGrid(
    analytics: EmotionAnalytics,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                title = "Дней дружбы",
                value = analytics.friendshipDays.toString(),
                icon = "📅",
                modifier = Modifier.weight(1f)
            )
            
            StatCard(
                title = "Всего эмоций",
                value = analytics.totalInteractions.toString(),
                icon = "💭",
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                title = "В день",
                value = String.format("%.1f", analytics.averageEmotionsPerDay),
                icon = "📊",
                modifier = Modifier.weight(1f)
            )
            
            StatCard(
                title = "Основная эмоция",
                value = analytics.dominantEmotion?.let { getEmotionEmoji(it) } ?: "-",
                icon = "",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon.isNotEmpty()) {
                Text(
                    text = icon,
                    fontSize = 20.sp
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun WeeklyEmotionChart(
    weeklyData: List<Pair<String, Map<EmotionType, Int>>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Эмоции за неделю",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                if (weeklyData.isEmpty()) return@Canvas
                
                val maxValue = weeklyData.maxOfOrNull { it.second.values.sum() } ?: 1
                val barWidth = size.width / weeklyData.size * 0.8f
                val barSpacing = size.width / weeklyData.size * 0.2f
                val chartHeight = size.height * 0.8f
                
                weeklyData.forEachIndexed { index, (day, emotions) ->
                    val x = index * (barWidth + barSpacing) + barSpacing / 2
                    var stackHeight = 0f
                    
                    EmotionType.values().forEach { emotion ->
                        val count = emotions[emotion] ?: 0
                        if (count > 0) {
                            val segmentHeight = (count.toFloat() / maxValue) * chartHeight
                            
                            drawRect(
                                color = getEmotionColor(emotion),
                                topLeft = Offset(x, size.height - stackHeight - segmentHeight),
                                size = Size(barWidth, segmentHeight)
                            )
                            
                            stackHeight += segmentHeight
                        }
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weeklyData.forEach { (day, _) ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

private fun getEmotionColor(emotion: EmotionType): Color {
    return when (emotion) {
        EmotionType.JOY -> Color(0xFFFFD700)
        EmotionType.SADNESS -> Color(0xFF4169E1)
        EmotionType.THOUGHTFUL -> Color(0xFF9370DB)

        EmotionType.NEUTRAL -> Color(0xFFCCCCCC)
    }
}

private fun getEmotionName(emotion: EmotionType): String {
    return when (emotion) {
        EmotionType.JOY -> "Радость"
        EmotionType.SADNESS -> "Грусть"
        EmotionType.THOUGHTFUL -> "Размышления"

        EmotionType.NEUTRAL -> "Нейтрально"
    }
}

private fun getEmotionEmoji(emotion: EmotionType): String {
    return when (emotion) {
        EmotionType.JOY -> "😊"
        EmotionType.SADNESS -> "😢"
        EmotionType.THOUGHTFUL -> "🤔"

        EmotionType.NEUTRAL -> "😐"
    }
}

private fun getBalanceColor(balance: Float): Color {
    return when {
        balance < 0.3f -> Color(0xFFE57373)
        balance < 0.7f -> Color(0xFFFFB74D)
        else -> Color(0xFF81C784)
    }
}

private fun getBalanceDescription(balance: Float): String {
    return when {
        balance < 0.3f -> "Нужно больше позитива"
        balance < 0.7f -> "Хороший эмоциональный баланс"
        else -> "Отличное эмоциональное состояние!"
    }
}

fun calculateEmotionAnalytics(
    emotionCounts: Map<EmotionType, Int>,
    friendshipDays: Int
): EmotionAnalytics {
    val totalInteractions = emotionCounts.values.sum()
    val averageEmotionsPerDay = if (friendshipDays > 0) {
        totalInteractions.toFloat() / friendshipDays
    } else 0f
    
    val dominantEmotion = emotionCounts.maxByOrNull { it.value }?.key
    
    val positiveEmotions = emotionCounts[EmotionType.JOY] ?: 0
    val negativeEmotions = emotionCounts[EmotionType.SADNESS] ?: 0
    val neutralEmotions = (emotionCounts[EmotionType.NEUTRAL] ?: 0) + 
                         (emotionCounts[EmotionType.THOUGHTFUL] ?: 0)
    
    val emotionBalance = if (totalInteractions > 0) {
        (positiveEmotions + neutralEmotions * 0.5f) / totalInteractions
    } else 0.5f
    
    return EmotionAnalytics(
        emotionCounts = emotionCounts,
        totalInteractions = totalInteractions,
        friendshipDays = friendshipDays,
        averageEmotionsPerDay = averageEmotionsPerDay,
        dominantEmotion = dominantEmotion,
        emotionBalance = emotionBalance.coerceIn(0f, 1f)
    )
}
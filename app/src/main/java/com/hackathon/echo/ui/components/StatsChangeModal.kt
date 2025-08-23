package com.hackathon.echo.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hackathon.echo.data.EmotionType
import com.hackathon.echo.data.PetStats
import com.hackathon.echo.ui.theme.Joy
import com.hackathon.echo.ui.theme.Neutral
import com.hackathon.echo.ui.theme.Sadness
import com.hackathon.echo.ui.theme.Thoughtful
import kotlinx.coroutines.delay

@Composable
fun StatsChangeModal(
    statsBefore: PetStats,
    statsAfter: PetStats,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var animationStarted by remember { mutableFloatStateOf(0f) }
    
    val scale by animateFloatAsState(
        targetValue = if (animationStarted > 0f) 1f else 0.8f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
        label = "modal_scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (animationStarted > 0f) 1f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "modal_alpha"
    )
    
    LaunchedEffect(Unit) {
        delay(100)
        animationStarted = 1f
    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth(0.9f)
                    .scale(scale)
                    .alpha(alpha),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "📊 Изменения состояния",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    
                    Text(
                        text = "Ваше общение повлияло на Эхо!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Статистика по каждой эмоции
                    EmotionStatRow(
                        emotion = EmotionType.JOY,
                        valueBefore = statsBefore.joy,
                        valueAfter = statsAfter.joy,
                        color = Joy,
                        label = "Радость",
                        emoji = "🌟"
                    )
                    
                    EmotionStatRow(
                        emotion = EmotionType.SADNESS,
                        valueBefore = statsBefore.sadness,
                        valueAfter = statsAfter.sadness,
                        color = Sadness,
                        label = "Грусть",
                        emoji = "💙"
                    )
                    
                    EmotionStatRow(
                        emotion = EmotionType.THOUGHTFUL,
                        valueBefore = statsBefore.thoughtful,
                        valueAfter = statsAfter.thoughtful,
                        color = Thoughtful,
                        label = "Размышления",
                        emoji = "💭"
                    )
                    
                    EmotionStatRow(
                        emotion = EmotionType.NEUTRAL,
                        valueBefore = statsBefore.neutral,
                        valueAfter = statsAfter.neutral,
                        color = Neutral,
                        label = "Спокойствие",
                        emoji = "😐"
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Модалка закроется автоматически",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun EmotionStatRow(
    emotion: EmotionType,
    valueBefore: Int,
    valueAfter: Int,
    color: Color,
    label: String,
    emoji: String,
    modifier: Modifier = Modifier
) {
    val difference = valueAfter - valueBefore
    
    if (difference == 0) return
    
    var animatedValue by remember { mutableFloatStateOf(valueBefore.toFloat()) }
    
    val progressValue by animateFloatAsState(
        targetValue = animatedValue / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "${emotion.name}_progress"
    )
    
    LaunchedEffect(Unit) {
        delay(300)
        animatedValue = valueAfter.toFloat()
    }
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = emoji,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$valueBefore",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "→",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "$valueAfter",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                
                if (difference > 0) {
                    Text(
                        text = "(+$difference)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "($difference)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE57373),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.Gray.copy(alpha = 0.2f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressValue)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}
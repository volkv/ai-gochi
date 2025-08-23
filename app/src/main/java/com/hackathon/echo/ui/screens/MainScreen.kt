package com.hackathon.echo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hackathon.echo.ui.components.ChatBubble
import com.hackathon.echo.ui.components.EmotionButtons
import com.hackathon.echo.ui.components.EmotionType
import com.hackathon.echo.ui.components.PetAvatar
import com.hackathon.echo.ui.components.PetState
import com.hackathon.echo.ui.components.RoomBackground
import com.hackathon.echo.ui.components.RoomState
import com.hackathon.echo.ui.components.WeatherState
import com.hackathon.echo.ui.components.PlantState
import com.hackathon.echo.ui.components.RoomLighting
import com.hackathon.echo.ui.theme.Calm
import com.hackathon.echo.ui.theme.Joy
import com.hackathon.echo.ui.theme.Sadness
import com.hackathon.echo.ui.theme.Thoughtful

@Composable
fun MainScreen() {
    var petState by remember { mutableStateOf(PetState()) }
    var roomState by remember { mutableStateOf(RoomState()) }
    var chatMessage by remember { mutableStateOf("") }
    var showChatBubble by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        RoomBackground(
            roomState = roomState,
            modifier = Modifier.fillMaxSize()
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                PetAvatar(
                    petState = petState
                )
                
                if (showChatBubble) {
                    Box(
                        modifier = Modifier.align(Alignment.TopCenter)
                    ) {
                        ChatBubble(
                            message = chatMessage,
                            isVisible = showChatBubble,
                            onDismiss = { showChatBubble = false }
                        )
                    }
                }
            }
            
            EmotionButtons(
                onEmotionSelected = { emotion ->
                    val (newPetState, newRoomState, message) = handleEmotionSelection(emotion)
                    petState = newPetState
                    roomState = newRoomState
                    chatMessage = message
                    showChatBubble = true
                }
            )
        }
    }
}

private fun handleEmotionSelection(emotion: EmotionType): Triple<PetState, RoomState, String> {
    return when (emotion) {
        EmotionType.JOY -> Triple(
            PetState(emotion = emotion, color = Joy),
            RoomState(
                weather = WeatherState.SUNNY,
                plant = PlantState.BLOOMING,
                lighting = RoomLighting.WARM
            ),
            "Твоя радость заряжает меня энергией! ✨"
        )
        EmotionType.SADNESS -> Triple(
            PetState(emotion = emotion, color = Sadness),
            RoomState(
                weather = WeatherState.CLOUDY,
                plant = PlantState.WITHERING,
                lighting = RoomLighting.COOL
            ),
            "Я здесь, чтобы тебя поддержать 💙"
        )
        EmotionType.THOUGHTFUL -> Triple(
            PetState(emotion = emotion, color = Thoughtful),
            RoomState(
                weather = WeatherState.CLOUDY,
                plant = PlantState.NORMAL,
                lighting = RoomLighting.NEUTRAL,
                hasCandle = true
            ),
            "Размышления помогают нам расти 🤔"
        )
        EmotionType.CALM -> Triple(
            PetState(emotion = emotion, color = Calm),
            RoomState(
                weather = WeatherState.SUNNY,
                plant = PlantState.NORMAL,
                lighting = RoomLighting.NEUTRAL
            ),
            "В тишине мы находим покой 🍃"
        )
        EmotionType.NEUTRAL -> Triple(
            PetState(emotion = emotion, color = androidx.compose.ui.graphics.Color.Gray),
            RoomState(),
            "Привет! Как дела?"
        )
    }
}
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
import com.hackathon.echo.data.EmotionType
import com.hackathon.echo.data.PetState
import com.hackathon.echo.data.PetStates
import com.hackathon.echo.data.WeatherState
import com.hackathon.echo.data.PlantState
import com.hackathon.echo.data.RoomLighting
import com.hackathon.echo.ui.components.ChatBubble
import com.hackathon.echo.ui.components.EmotionButtons
import com.hackathon.echo.ui.components.PetAvatar
import com.hackathon.echo.ui.components.RoomBackground
import com.hackathon.echo.ui.theme.Calm
import com.hackathon.echo.ui.theme.Joy
import com.hackathon.echo.ui.theme.Sadness
import com.hackathon.echo.ui.theme.Thoughtful

@Composable
fun MainScreen() {
    var petState by remember { mutableStateOf(PetStates.neutralState) }
    var chatMessage by remember { mutableStateOf("") }
    var showChatBubble by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        RoomBackground(
            petState = petState,
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
                    val (newPetState, message) = handleEmotionSelection(emotion)
                    petState = newPetState
                    chatMessage = message
                    showChatBubble = true
                }
            )
        }
    }
}

private fun handleEmotionSelection(emotion: EmotionType): Pair<PetState, String> {
    return when (emotion) {
        EmotionType.JOY -> Pair(
            PetStates.joyState,
            "Твоя радость заряжает меня энергией! ✨"
        )
        EmotionType.SADNESS -> Pair(
            PetStates.sadnessState,
            "Я здесь, чтобы тебя поддержать 💙"
        )
        EmotionType.THOUGHTFUL -> Pair(
            PetStates.thoughtfulState,
            "Размышления помогают нам расти 🤔"
        )
        EmotionType.CALM -> Pair(
            PetStates.calmState,
            "В тишине мы находим покой 🍃"
        )
        EmotionType.NEUTRAL -> Pair(
            PetStates.neutralState,
            "Привет! Как дела?"
        )
    }
}
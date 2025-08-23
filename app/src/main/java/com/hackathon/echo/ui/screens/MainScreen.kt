package com.hackathon.echo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hackathon.echo.data.EmotionType
import com.hackathon.echo.ui.components.ChatBubble
import com.hackathon.echo.ui.components.ChatInterface
import com.hackathon.echo.ui.components.PetAvatar
import com.hackathon.echo.ui.components.PetStatsBar
import com.hackathon.echo.ui.components.RoomBackground
import com.hackathon.echo.ui.components.StatsChangeModal
import com.hackathon.echo.ui.theme.Thoughtful
import com.hackathon.echo.viewmodel.EchoViewModel
import com.hackathon.echo.viewmodel.EchoViewModelFactory

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val viewModel: EchoViewModel = viewModel(factory = EchoViewModelFactory(context))
    
    val petState by viewModel.currentPetState.collectAsState()
    val petStats by viewModel.petStats.collectAsState()
    val currentResponse by viewModel.currentResponse.collectAsState()
    val isChatOpen by viewModel.isChatOpen.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val currentDemoStep by viewModel.currentDemoStep.collectAsState()
    val showStatsChange by viewModel.showStatsChange.collectAsState()
    val statsChangeBefore by viewModel.statsChangeBefore.collectAsState()
    val statsChangeAfter by viewModel.statsChangeAfter.collectAsState()
    
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
            // Питомец в центре экрана
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                PetAvatar(
                    petState = petState
                )
                
                if (currentResponse.isNotEmpty()) {
                    Box(
                        modifier = Modifier.align(Alignment.TopCenter)
                    ) {
                        ChatBubble(
                            message = currentResponse,
                            isVisible = currentResponse.isNotEmpty(),
                            onDismiss = { /* Пусто, сообщение исчезнет автоматически */ }
                        )
                    }
                }
            }
            
            // Шкалы состояния питомца
            PetStatsBar(
                petStats = petStats,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Кнопка чата
            Button(
                onClick = { viewModel.openChat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Thoughtful,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "💬 Рассказать",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        ChatInterface(
            isOpen = isChatOpen,
            messages = chatMessages,
            onSendMessage = { message ->
                viewModel.sendChatMessage(message)
            },
            onClose = { viewModel.closeChat() },
            onDemoPhrase = { phrase ->
                if (phrase.isEmpty()) {
                    viewModel.fillDemoPhrase()
                } else {
                    viewModel.fillDemoPhrase(phrase)
                }
            },
            currentDemoStep = currentDemoStep
        )
        
        if (showStatsChange && statsChangeBefore != null && statsChangeAfter != null) {
            StatsChangeModal(
                statsBefore = statsChangeBefore!!,
                statsAfter = statsChangeAfter!!,
                onDismiss = { viewModel.hideStatsChangeModal() }
            )
        }
    }
}


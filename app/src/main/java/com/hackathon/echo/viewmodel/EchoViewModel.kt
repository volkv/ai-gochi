package com.hackathon.echo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hackathon.echo.data.EmotionType
import com.hackathon.echo.data.PetState
import com.hackathon.echo.data.PetStates
import com.hackathon.echo.data.ResponseBank
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

data class InteractionHistory(
    val timestamp: Long,
    val userInput: String,
    val detectedEmotion: EmotionType,
    val echoResponse: String,
    val petStateBefore: PetState,
    val petStateAfter: PetState
)

class EchoViewModel : ViewModel() {
    
    private val _currentPetState = MutableStateFlow(PetStates.neutralState)
    val currentPetState: StateFlow<PetState> = _currentPetState.asStateFlow()
    
    private val _interactionHistory = MutableLiveData<List<InteractionHistory>>()
    val interactionHistory: LiveData<List<InteractionHistory>> = _interactionHistory
    
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String> = _currentResponse.asStateFlow()
    
    private val _isProcessingInput = MutableStateFlow(false)
    val isProcessingInput: StateFlow<Boolean> = _isProcessingInput.asStateFlow()
    
    init {
        _interactionHistory.value = emptyList()
    }
    
    fun processEmotionButton(emotion: EmotionType) {
        val previousState = _currentPetState.value
        val newState = PetStates.getStateByEmotion(emotion)
        val response = ResponseBank.getRandomResponse(emotion)
        
        _currentPetState.value = newState
        _currentResponse.value = response
        
        addToHistory(
            userInput = "Нажата кнопка: ${getEmotionButtonText(emotion)}",
            detectedEmotion = emotion,
            echoResponse = response,
            petStateBefore = previousState,
            petStateAfter = newState
        )
    }
    
    fun processUserInput(text: String) {
        if (text.isBlank()) return
        
        _isProcessingInput.value = true
        
        val previousState = _currentPetState.value
        val detectedEmotion = detectEmotionFromText(text)
        val newState = PetStates.getStateByEmotion(detectedEmotion)
        val response = getContextualResponse(text, detectedEmotion)
        
        _currentPetState.value = newState
        _currentResponse.value = response
        _isProcessingInput.value = false
        
        addToHistory(
            userInput = text,
            detectedEmotion = detectedEmotion,
            echoResponse = response,
            petStateBefore = previousState,
            petStateAfter = newState
        )
    }
    
    fun resetToNeutral() {
        _currentPetState.value = PetStates.neutralState
        _currentResponse.value = ResponseBank.getRandomResponse(EmotionType.NEUTRAL)
    }
    
    fun getRandomResponseForCurrentEmotion(): String {
        return ResponseBank.getRandomResponse(_currentPetState.value.emotion)
    }
    
    fun getLastNInteractions(n: Int): List<InteractionHistory> {
        return _interactionHistory.value?.takeLast(n) ?: emptyList()
    }
    
    fun getTotalInteractionsCount(): Int {
        return _interactionHistory.value?.size ?: 0
    }
    
    fun getEmotionStatistics(): Map<EmotionType, Int> {
        val history = _interactionHistory.value ?: return emptyMap()
        return history.groupBy { it.detectedEmotion }
            .mapValues { it.value.size }
    }
    
    private fun detectEmotionFromText(text: String): EmotionType {
        val lowercaseText = text.lowercase(Locale.getDefault())
        
        val joyKeywords = listOf(
            "радость", "счастлив", "отлично", "здорово", "супер", "прекрасно", 
            "восторг", "ура", "победа", "успех", "достижение", "поздравь", "праздник"
        )
        
        val sadnessKeywords = listOf(
            "грустно", "печально", "расстроен", "переживаю", "тревога", "проблема",
            "болит", "тяжело", "плохо", "устал", "депрессия", "одиноко", "страшно"
        )
        
        val thoughtfulKeywords = listOf(
            "думаю", "размышляю", "интересно", "будущее", "философия", "смысл",
            "вопрос", "почему", "задаюсь", "мысли", "анализ", "понимание", "мудрость"
        )
        
        val calmKeywords = listOf(
            "спокойно", "тишина", "покой", "умиротворен", "благодарен", "медитация",
            "релакс", "дышу", "наслаждаюсь", "гармония", "баланс", "мир", "zen"
        )
        
        var joyScore = 0
        var sadnessScore = 0
        var thoughtfulScore = 0
        var calmScore = 0
        
        joyKeywords.forEach { keyword ->
            if (lowercaseText.contains(keyword)) joyScore++
        }
        
        sadnessKeywords.forEach { keyword ->
            if (lowercaseText.contains(keyword)) sadnessScore++
        }
        
        thoughtfulKeywords.forEach { keyword ->
            if (lowercaseText.contains(keyword)) thoughtfulScore++
        }
        
        calmKeywords.forEach { keyword ->
            if (lowercaseText.contains(keyword)) calmScore++
        }
        
        val maxScore = maxOf(joyScore, sadnessScore, thoughtfulScore, calmScore)
        
        return when {
            maxScore == 0 -> EmotionType.NEUTRAL
            joyScore == maxScore -> EmotionType.JOY
            sadnessScore == maxScore -> EmotionType.SADNESS  
            thoughtfulScore == maxScore -> EmotionType.THOUGHTFUL
            calmScore == maxScore -> EmotionType.CALM
            else -> EmotionType.NEUTRAL
        }
    }
    
    private fun getContextualResponse(userInput: String, emotion: EmotionType): String {
        val history = _interactionHistory.value ?: emptyList()
        val recentEmotions = history.takeLast(3).map { it.detectedEmotion }
        
        return when {
            recentEmotions.contains(emotion) && recentEmotions.size >= 2 -> {
                getPersonalizedResponse(emotion, true)
            }
            userInput.length > 50 -> {
                getDetailedResponse(emotion)
            }
            else -> {
                ResponseBank.getRandomResponse(emotion)
            }
        }
    }
    
    private fun getPersonalizedResponse(emotion: EmotionType, isRepeat: Boolean): String {
        return when (emotion) {
            EmotionType.JOY -> if (isRepeat) {
                "Вижу, радость не покидает тебя! Это прекрасно! ✨"
            } else {
                ResponseBank.getRandomResponse(emotion)
            }
            EmotionType.SADNESS -> if (isRepeat) {
                "Я по-прежнему с тобой. Давай вместе пройдем через это... 💙"
            } else {
                ResponseBank.getRandomResponse(emotion)
            }
            EmotionType.THOUGHTFUL -> if (isRepeat) {
                "Ты продолжаешь размышлять... Это путь к глубокой мудрости 🧠"
            } else {
                ResponseBank.getRandomResponse(emotion)
            }
            EmotionType.CALM -> if (isRepeat) {
                "Твое спокойствие становится еще глубже... 🧘"
            } else {
                ResponseBank.getRandomResponse(emotion)
            }
            else -> ResponseBank.getRandomResponse(emotion)
        }
    }
    
    private fun getDetailedResponse(emotion: EmotionType): String {
        return when (emotion) {
            EmotionType.JOY -> "Так много деталей! Я чувствую, как важна для тебя эта радость! ⭐"
            EmotionType.SADNESS -> "Спасибо, что доверяешь мне свои переживания. Я внимательно слушаю... 💙"
            EmotionType.THOUGHTFUL -> "Какие глубокие размышления! Твои мысли заставляют и меня задуматься 💭"
            EmotionType.CALM -> "Твое подробное описание успокаивает и меня. Мы в гармонии 🌸"
            else -> "Спасибо за то, что так подробно поделился со мной!"
        }
    }
    
    private fun addToHistory(
        userInput: String,
        detectedEmotion: EmotionType,
        echoResponse: String,
        petStateBefore: PetState,
        petStateAfter: PetState
    ) {
        val currentHistory = _interactionHistory.value ?: emptyList()
        val newInteraction = InteractionHistory(
            timestamp = System.currentTimeMillis(),
            userInput = userInput,
            detectedEmotion = detectedEmotion,
            echoResponse = echoResponse,
            petStateBefore = petStateBefore,
            petStateAfter = petStateAfter
        )
        
        val updatedHistory = currentHistory + newInteraction
        val limitedHistory = if (updatedHistory.size > 50) {
            updatedHistory.takeLast(50)
        } else {
            updatedHistory
        }
        
        _interactionHistory.value = limitedHistory
    }
    
    private fun getEmotionButtonText(emotion: EmotionType): String {
        return when (emotion) {
            EmotionType.JOY -> "Поделиться радостью"
            EmotionType.SADNESS -> "Рассказать о тревоге"
            EmotionType.THOUGHTFUL -> "Поразмышлять"
            EmotionType.CALM -> "Побыть в тишине"
            EmotionType.NEUTRAL -> "Нейтрально"
        }
    }
}
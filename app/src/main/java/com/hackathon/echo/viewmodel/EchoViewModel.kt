package com.hackathon.echo.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hackathon.echo.data.*
import com.hackathon.echo.ui.components.ChatMessage
import com.hackathon.echo.ui.components.EmotionAnalytics
import com.hackathon.echo.ui.components.calculateEmotionAnalytics
import com.hackathon.echo.utils.SoundManager
import kotlinx.coroutines.*
import kotlinx.coroutines.Job
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

class EchoViewModel(private val context: Context) : ViewModel() {
    
    private val preferences = EchoPreferences(context)
    private val memorySystem = MemorySystem(context)
    
    private val _currentPetState = MutableStateFlow(PetStates.neutralState)
    val currentPetState: StateFlow<PetState> = _currentPetState.asStateFlow()
    
    private val _interactionHistory = MutableLiveData<List<InteractionHistory>>()
    val interactionHistory: LiveData<List<InteractionHistory>> = _interactionHistory
    
    private val _currentResponse = MutableStateFlow("")
    val currentResponse: StateFlow<String> = _currentResponse.asStateFlow()
    
    private val _isProcessingInput = MutableStateFlow(false)
    val isProcessingInput: StateFlow<Boolean> = _isProcessingInput.asStateFlow()
    
    private val _personalizedGreeting = MutableStateFlow<PersonalizedGreeting?>(null)
    val personalizedGreeting: StateFlow<PersonalizedGreeting?> = _personalizedGreeting.asStateFlow()
    
    private val _emotionAnalytics = MutableStateFlow<EmotionAnalytics?>(null)
    val emotionAnalytics: StateFlow<EmotionAnalytics?> = _emotionAnalytics.asStateFlow()
    
    private val _achievements = MutableLiveData<List<Achievement>>()
    val achievements: LiveData<List<Achievement>> = _achievements
    
    private val _petStats = MutableStateFlow(preferences.getPetStats())
    val petStats: StateFlow<PetStats> = _petStats.asStateFlow()
    
    private val _isChatOpen = MutableStateFlow(false)
    val isChatOpen: StateFlow<Boolean> = _isChatOpen.asStateFlow()
    
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()
    
    private val _currentDemoStep = MutableStateFlow(0)
    val currentDemoStep: StateFlow<Int> = _currentDemoStep.asStateFlow()
    
    private val _showStatsChange = MutableStateFlow(false)
    val showStatsChange: StateFlow<Boolean> = _showStatsChange.asStateFlow()
    
    private val _statsChangeBefore = MutableStateFlow<PetStats?>(null)
    val statsChangeBefore: StateFlow<PetStats?> = _statsChangeBefore.asStateFlow()
    
    private val _statsChangeAfter = MutableStateFlow<PetStats?>(null)
    val statsChangeAfter: StateFlow<PetStats?> = _statsChangeAfter.asStateFlow()
    
    private val _isPetTyping = MutableStateFlow(false)
    val isPetTyping: StateFlow<Boolean> = _isPetTyping.asStateFlow()
    
    private val _isAutoDemo = MutableStateFlow(false)
    val isAutoDemo: StateFlow<Boolean> = _isAutoDemo.asStateFlow()
    
    private var autoDemoJob: Job? = null
    
    init {
        _interactionHistory.value = emptyList()
        SoundManager.initialize(context)
        initializeAppSession()
    }
    
    private fun initializeAppSession() {
        preferences.updateFriendshipDays()
        generateInitialGreeting()
        updateEmotionAnalytics()
        _achievements.value = preferences.getAchievements()
    }
    
    private fun generateInitialGreeting() {
        val greeting = memorySystem.generatePersonalizedGreeting()
        _personalizedGreeting.value = greeting
        if (greeting.shouldShow) {
            _currentResponse.value = greeting.greeting
        }
    }
    
    fun processEmotionButton(emotion: EmotionType) {
        val previousState = _currentPetState.value
        val newState = PetStates.getStateByEmotion(emotion)
        val response = memorySystem.getPersonalizedResponse(emotion, "Кнопка эмоций")
        
        _currentPetState.value = newState
        _currentResponse.value = response
        
        SoundManager.playEmotionSound(emotion)
        
        val userInputText = "Нажата кнопка: ${getEmotionButtonText(emotion)}"
        
        addToHistory(
            userInput = userInputText,
            detectedEmotion = emotion,
            echoResponse = response,
            petStateBefore = previousState,
            petStateAfter = newState
        )
        
        saveInteractionToPreferences(emotion, userInputText)
        updatePetStats(emotion, userInputText)
        updateStats()
        
        val reflectiveQuestion = memorySystem.getReflectiveQuestion(emotion)
        if (reflectiveQuestion != null && _currentResponse.value == response) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(3000)
                if (_currentResponse.value == response) {
                    _currentResponse.value = reflectiveQuestion
                }
            }
        }
    }
    
    fun processUserInput(text: String) {
        if (text.isBlank()) return
        
        _isProcessingInput.value = true
        
        val previousState = _currentPetState.value
        val detectedEmotion = detectEmotionFromText(text)
        val newState = PetStates.getStateByEmotion(detectedEmotion)
        val response = memorySystem.getPersonalizedResponse(detectedEmotion, text)
        
        _currentPetState.value = newState
        _currentResponse.value = response
        _isProcessingInput.value = false
        
        SoundManager.playEmotionSound(detectedEmotion)
        
        addToHistory(
            userInput = text,
            detectedEmotion = detectedEmotion,
            echoResponse = response,
            petStateBefore = previousState,
            petStateAfter = newState
        )
        
        saveInteractionToPreferences(detectedEmotion, text)
        updatePetStats(detectedEmotion, text)
        updateStats()
        
        val reflectiveQuestion = memorySystem.getReflectiveQuestion(detectedEmotion)
        if (reflectiveQuestion != null) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(4000)
                if (_currentResponse.value == response) {
                    _currentResponse.value = reflectiveQuestion
                }
            }
        }
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
    
    fun getFriendshipDays(): Int {
        return preferences.getFriendshipDays()
    }
    
    fun getStoredEmotionHistory(): List<StoredEmotion> {
        return preferences.getEmotionHistory()
    }
    
    fun getUnlockedAchievements(): List<Achievement> {
        return preferences.getAchievements().filter { it.isUnlocked }
    }
    
    fun refreshGreeting() {
        generateInitialGreeting()
    }
    
    fun getUsageStats(): UsageStats {
        return preferences.getUsageStats()
    }
    
    fun updateSoundSettings(soundEnabled: Boolean = true, vibrationEnabled: Boolean = true, volume: Float = 0.5f) {
        val settings = com.hackathon.echo.utils.SoundSettings(
            isSoundEnabled = soundEnabled,
            isVibrationEnabled = vibrationEnabled,
            soundVolume = volume
        )
        SoundManager.updateSettings(settings)
    }
    
    fun updatePetStats(emotion: EmotionType, userInput: String = "") {
        val currentStats = _petStats.value
        var newStats = PetStats.increase(currentStats, emotion)
        
        // Проверяем на эмпатичность сообщения
        if (userInput.isNotEmpty() && detectEmpathyFromText(userInput)) {
            newStats = PetStats.increaseEmpathy(newStats)
        }
        
        _petStats.value = newStats
        
        // Сохраняем статистики в preferences
        preferences.savePetStats(newStats)
    }
    
    fun decreasePetStats(emotion: EmotionType) {
        val currentStats = _petStats.value
        val newStats = PetStats.decrease(currentStats, emotion)
        _petStats.value = newStats
        
        // Сохраняем статистики в preferences
        preferences.savePetStats(newStats)
    }
    
    fun getCurrentPetStats(): PetStats {
        return _petStats.value
    }
    
    fun testVibration(emotion: EmotionType) {
        SoundManager.testVibration(emotion)
    }
    
    fun openChat() {
        _isChatOpen.value = true
        if (_chatMessages.value.isEmpty()) {
            addChatMessage(
                ChatMessage(
                    text = "Привет! 👋 Расскажи мне, что у тебя на душе?",
                    isFromUser = false
                )
            )
        }
    }
    
    fun closeChat() {
        _isChatOpen.value = false
    }
    
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        
        val statsBefore = _petStats.value
        addChatMessage(ChatMessage(text = text.trim(), isFromUser = true))
        
        // Показываем typing indicator
        _isPetTyping.value = true
        
        CoroutineScope(Dispatchers.Main).launch {
            // Симуляция "размышления" питомца перед ответом
            delay((1000..2000).random().toLong())
            
            // Обрабатываем ответ питомца
            processUserInput(text.trim())
            
            val response = _currentResponse.value
            if (response.isNotEmpty()) {
                // Симуляция "набора" ответа
                delay((800..1500).random().toLong())
                
                // Скрываем typing indicator и показываем ответ
                _isPetTyping.value = false
                addChatMessage(ChatMessage(text = response, isFromUser = false))
                
                // Показываем модалку изменения статистик с задержкой
                delay(2000)
                val statsAfter = _petStats.value
                if (statsBefore != statsAfter) {
                    showStatsChangeModal(statsBefore, statsAfter)
                }
            } else {
                _isPetTyping.value = false
            }
        }
    }
    
    fun addChatMessage(message: ChatMessage) {
        val currentMessages = _chatMessages.value.toMutableList()
        currentMessages.add(message)
        _chatMessages.value = currentMessages
    }
    
    fun clearChatHistory() {
        _chatMessages.value = emptyList()
        _currentDemoStep.value = 0
    }
    
    fun nextDemoStep() {
        _currentDemoStep.value = (_currentDemoStep.value + 1) % 4
    }
    
    fun fillDemoPhrase(phrase: String) {
        val detectedEmotion = detectEmotionFromText(phrase)
        val scriptedResponse = DemoScriptedPhrases.getScriptedResponseForMessage(phrase, detectedEmotion)
        
        addChatMessage(ChatMessage(text = phrase, isFromUser = true))
        
        val statsBefore = _petStats.value
        
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000)
            
            val previousState = _currentPetState.value
            val newState = PetStates.getStateByEmotion(detectedEmotion)
            
            _currentPetState.value = newState
            SoundManager.playEmotionSound(detectedEmotion)
            
            val response = scriptedResponse ?: memorySystem.getPersonalizedResponse(detectedEmotion, phrase)
            _currentResponse.value = response
            
            addToHistory(
                userInput = phrase,
                detectedEmotion = detectedEmotion,
                echoResponse = response,
                petStateBefore = previousState,
                petStateAfter = newState
            )
            
            saveInteractionToPreferences(detectedEmotion, phrase)
            updatePetStats(detectedEmotion, phrase)
            updateStats()
            
            addChatMessage(ChatMessage(text = response, isFromUser = false))
            
            delay(2000)
            
            val statsAfter = _petStats.value
            if (statsBefore != statsAfter) {
                showStatsChangeModal(statsBefore, statsAfter)
            }
            
            nextDemoStep()
        }
    }
    
    fun fillDemoPhrase() {
        val currentStep = _currentDemoStep.value
        val demoPhrase = DemoScriptedPhrases.getDemoPhraseByCycle(currentStep)
        fillDemoPhrase(demoPhrase)
    }
    
    fun startAutoDemo() {
        if (_isAutoDemo.value) return
        
        _isAutoDemo.value = true
        _currentDemoStep.value = 0
        
        autoDemoJob = CoroutineScope(Dispatchers.Main).launch {
            repeat(4) { step ->
                if (!_isAutoDemo.value) return@launch
                
                val demoPair = DemoScriptedPhrases.getDemoMessagePairByCycle(step)
                val emotion = DemoScriptedPhrases.getDemoEmotionByCycle(step)
                
                addChatMessage(ChatMessage(text = demoPair.userMessage, isFromUser = true))
                
                val statsBefore = _petStats.value
                
                delay(1500)
                
                val previousState = _currentPetState.value
                val newState = PetStates.getStateByEmotion(emotion)
                
                _currentPetState.value = newState
                SoundManager.playEmotionSound(emotion)
                
                val response = demoPair.petResponse
                _currentResponse.value = response
                
                addToHistory(
                    userInput = demoPair.userMessage,
                    detectedEmotion = emotion,
                    echoResponse = response,
                    petStateBefore = previousState,
                    petStateAfter = newState
                )
                
                saveInteractionToPreferences(emotion, demoPair.userMessage)
                updatePetStats(emotion, demoPair.userMessage)
                updateStats()
                
                addChatMessage(ChatMessage(text = response, isFromUser = false))
                
                delay(2000)
                
                val statsAfter = _petStats.value
                if (statsBefore != statsAfter) {
                    showStatsChangeModal(statsBefore, statsAfter)
                    delay(3000)
                }
                
                _currentDemoStep.value = step + 1
                
                if (step < 3) {
                    delay(1500)
                }
            }
            
            _isAutoDemo.value = false
        }
    }
    
    fun stopAutoDemo() {
        _isAutoDemo.value = false
        autoDemoJob?.cancel()
        autoDemoJob = null
    }
    
    fun getCurrentDemoEmotion(): EmotionType {
        return DemoScriptedPhrases.getDemoEmotionByCycle(_currentDemoStep.value)
    }
    
    fun showStatsChangeModal(before: PetStats, after: PetStats) {
        _statsChangeBefore.value = before
        _statsChangeAfter.value = after
        _showStatsChange.value = true
        
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            hideStatsChangeModal()
        }
    }
    
    fun hideStatsChangeModal() {
        _showStatsChange.value = false
        _statsChangeBefore.value = null
        _statsChangeAfter.value = null
    }
    
    fun clearAllData() {
        preferences.clearAllData()
        _interactionHistory.value = emptyList()
        _currentResponse.value = "Привет! Я Эхо, приятно познакомиться!"
        _personalizedGreeting.value = null
        _emotionAnalytics.value = null
        _achievements.value = preferences.getAchievements()
        updateEmotionAnalytics()
    }
    
    private fun saveInteractionToPreferences(emotion: EmotionType, userInput: String) {
        preferences.saveEmotionToHistory(emotion, userInput)
    }
    
    private fun updateStats() {
        val emotionStats = getEmotionStatistics()
        val totalInteractions = getTotalInteractionsCount()
        preferences.updateUsageStats(emotionStats, totalInteractions)
        
        val newAchievements = preferences.getAchievements()
        val oldAchievements = _achievements.value ?: emptyList()
        
        val newlyUnlocked = newAchievements.filter { new ->
            new.isUnlocked && oldAchievements.none { old -> old.id == new.id && old.isUnlocked }
        }
        
        if (newlyUnlocked.isNotEmpty()) {
            _achievements.value = newAchievements
            newlyUnlocked.forEach { achievement ->
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    _currentResponse.value = "🎉 Достижение разблокировано: ${achievement.name}! ${achievement.description}"
                }
            }
        } else {
            _achievements.value = newAchievements
        }
        
        updateEmotionAnalytics()
    }
    
    private fun updateEmotionAnalytics() {
        val emotionStats = getEmotionStatistics()
        val friendshipDays = preferences.getFriendshipDays()
        val analytics = calculateEmotionAnalytics(emotionStats, friendshipDays)
        _emotionAnalytics.value = analytics
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
        

        
        var joyScore = 0
        var sadnessScore = 0
        var thoughtfulScore = 0
        
        joyKeywords.forEach { keyword ->
            if (lowercaseText.contains(keyword)) joyScore++
        }
        
        sadnessKeywords.forEach { keyword ->
            if (lowercaseText.contains(keyword)) sadnessScore++
        }
        
        thoughtfulKeywords.forEach { keyword ->
            if (lowercaseText.contains(keyword)) thoughtfulScore++
        }
        
        val maxScore = maxOf(joyScore, sadnessScore, thoughtfulScore)
        
        return when {
            maxScore == 0 -> EmotionType.NEUTRAL
            joyScore == maxScore -> EmotionType.JOY
            sadnessScore == maxScore -> EmotionType.SADNESS  
            thoughtfulScore == maxScore -> EmotionType.THOUGHTFUL
            else -> EmotionType.NEUTRAL
        }
    }
    
    private fun detectEmpathyFromText(text: String): Boolean {
        val lowercaseText = text.lowercase(Locale.getDefault())
        
        val empathyKeywords = listOf(
            "чувствую", "переживаю", "волнуюсь", "боюсь", "мечтаю", "надеюсь",
            "беспокоюсь", "тревожусь", "сердце", "душа", "эмоции", "откровенно",
            "поделиться", "доверяю", "искренне", "честно говоря", "по секрету",
            "лично", "интимно", "глубоко", "сокровенное", "признаюсь"
        )
        
        val empathyScore = empathyKeywords.count { keyword ->
            lowercaseText.contains(keyword)
        }
        
        // Считается эмпатичным, если содержит ключевые слова эмпатии
        // или если сообщение длинное (>50 символов) - значит пользователь делится чем-то личным
        return empathyScore > 0 || text.length > 50
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

            else -> ResponseBank.getRandomResponse(emotion)
        }
    }
    
    private fun getDetailedResponse(emotion: EmotionType): String {
        return when (emotion) {
            EmotionType.JOY -> "Так много деталей! Я чувствую, как важна для тебя эта радость! ⭐"
            EmotionType.SADNESS -> "Спасибо, что доверяешь мне свои переживания. Я внимательно слушаю... 💙"
            EmotionType.THOUGHTFUL -> "Какие глубокие размышления! Твои мысли заставляют и меня задуматься 💭"

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

            EmotionType.NEUTRAL -> "Нейтрально"
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        stopAutoDemo()
        SoundManager.release()
    }
}

class EchoViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EchoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EchoViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
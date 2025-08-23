package com.hackathon.echo.data

import android.content.Context
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

data class PersonalizedGreeting(
    val greeting: String,
    val shouldShow: Boolean = true,
    val priority: Int = 0
)

class MemorySystem(private val context: Context) {
    private val preferences = EchoPreferences(context)
    
    fun generatePersonalizedGreeting(): PersonalizedGreeting {
        if (!preferences.isPersonalizedGreetingsEnabled()) {
            return PersonalizedGreeting(getDefaultGreeting(), false)
        }
        
        val lastInteractionDate = preferences.getLastInteractionDate()
        val friendshipDays = preferences.getFriendshipDays()
        val recentEmotions = preferences.getRecentEmotions(3)
        val usageStats = preferences.getUsageStats()
        
        return when {
            isFirstMeeting() -> PersonalizedGreeting(
                "Привет! Я Эхо, твой эмоциональный спутник. Расскажи, как у тебя дела?",
                priority = 10
            )
            
            isLongTimeNoSee(lastInteractionDate) -> PersonalizedGreeting(
                generateLongAbsenceGreeting(lastInteractionDate),
                priority = 9
            )
            
            isNewDay(lastInteractionDate) -> PersonalizedGreeting(
                generateNewDayGreeting(recentEmotions, friendshipDays),
                priority = 8
            )
            
            hasRecentEmotions(recentEmotions) -> PersonalizedGreeting(
                generateEmotionBasedGreeting(recentEmotions),
                priority = 7
            )
            
            hasMilestoneAchievement(usageStats) -> PersonalizedGreeting(
                generateMilestoneGreeting(friendshipDays, usageStats),
                priority = 6
            )
            
            else -> PersonalizedGreeting(
                getContextualGreeting(usageStats),
                priority = 5
            )
        }
    }
    
    fun getPersonalizedResponse(currentEmotion: EmotionType, userInput: String): String {
        val recentEmotions = preferences.getRecentEmotions(5)
        val usageStats = preferences.getUsageStats()
        
        return when {
            hasEmotionalPattern(recentEmotions, currentEmotion) -> 
                getPatternBasedResponse(currentEmotion, recentEmotions)
            
            isDetailedInput(userInput) -> 
                getDetailedInputResponse(currentEmotion, userInput)
            
            isFirstTimeEmotion(currentEmotion, usageStats) ->
                getFirstTimeEmotionResponse(currentEmotion)
            
            else -> ResponseBank.getRandomResponse(currentEmotion)
        }
    }
    
    fun getReflectiveQuestion(emotion: EmotionType): String? {
        val recentEmotions = preferences.getRecentEmotions(3)
        if (recentEmotions.isEmpty()) return null
        
        val lastEmotion = try {
            EmotionType.valueOf(recentEmotions.last().emotion)
        } catch (e: Exception) {
            return null
        }
        
        return when {
            lastEmotion != emotion && isSignificantEmotionChange(lastEmotion, emotion) -> {
                getTransitionQuestion(lastEmotion, emotion)
            }
            
            recentEmotions.size >= 2 && allSameEmotion(recentEmotions) -> {
                getContinuityQuestion(emotion)
            }
            
            else -> null
        }
    }
    
    private fun isFirstMeeting(): Boolean {
        return preferences.getFriendshipDays() == 0 && 
               preferences.getUsageStats().totalInteractions == 0
    }
    
    private fun isLongTimeNoSee(lastInteractionDate: LocalDate?): Boolean {
        if (lastInteractionDate == null) return false
        val daysSinceLastInteraction = ChronoUnit.DAYS.between(lastInteractionDate, LocalDate.now())
        return daysSinceLastInteraction >= 3
    }
    
    private fun isNewDay(lastInteractionDate: LocalDate?): Boolean {
        if (lastInteractionDate == null) return false
        return !lastInteractionDate.isEqual(LocalDate.now())
    }
    
    private fun hasRecentEmotions(recentEmotions: List<StoredEmotion>): Boolean {
        return recentEmotions.isNotEmpty()
    }
    
    private fun hasMilestoneAchievement(stats: UsageStats): Boolean {
        val friendshipDays = preferences.getFriendshipDays()
        return friendshipDays in listOf(1, 7, 30, 100) || 
               stats.totalInteractions in listOf(10, 25, 50, 100)
    }
    
    private fun generateLongAbsenceGreeting(lastInteractionDate: LocalDate?): String {
        val daysSince = ChronoUnit.DAYS.between(lastInteractionDate, LocalDate.now()).toInt()
        
        return when {
            daysSince >= 7 -> "С возвращением! Прошла целая неделя! Как ты провел это время? Скучал по нашим беседам 🌟"
            daysSince >= 3 -> "Привет! Давно не виделись! Как дела? Что нового произошло за эти дни?"
            else -> "О, ты вернулся! Рад тебя снова видеть! 😊"
        }
    }
    
    private fun generateNewDayGreeting(recentEmotions: List<StoredEmotion>, friendshipDays: Int): String {
        val lastEmotion = recentEmotions.lastOrNull()
        
        return when {
            lastEmotion != null -> {
                val emotionType = try {
                    EmotionType.valueOf(lastEmotion.emotion)
                } catch (e: Exception) {
                    null
                }
                
                when (emotionType) {
                    EmotionType.JOY -> "Доброе утро! Помню, вчера ты делился радостью! Надеюсь, хорошее настроение сохранилось? ✨"
                    EmotionType.SADNESS -> "Привет... Как дела с тем, что вчера тебя расстраивало? Я здесь, чтобы поддержать 💙"
                    EmotionType.THOUGHTFUL -> "Привет, мыслитель! Как дела с теми размышлениями, что занимали тебя вчера? 🤔"
                    EmotionType.CALM -> "Доброе утро! Надеюсь, то спокойствие, что было у тебя вчера, остается с тобой 🕊️"
                    else -> getTimeBasedGreeting(friendshipDays)
                }
            }
            else -> getTimeBasedGreeting(friendshipDays)
        }
    }
    
    private fun generateEmotionBasedGreeting(recentEmotions: List<StoredEmotion>): String {
        val dominantEmotion = recentEmotions
            .groupBy { it.emotion }
            .maxByOrNull { it.value.size }?.key
        
        return when (dominantEmotion) {
            "JOY" -> "Привет! Вижу, в последнее время у тебя много радостных моментов! Продолжай делиться ✨"
            "SADNESS" -> "Здравствуй... Замечаю, что в последнее время тебе нелегко. Я рядом, расскажи что происходит 💙"
            "THOUGHTFUL" -> "Привет, философ! Много размышляешь в последнее время. О чем думаешь сегодня? 🧠"
            "CALM" -> "Приветствую! Чувствую твое внутреннее спокойствие. Приятно видеть тебя в гармонии 🌸"
            else -> getDefaultGreeting()
        }
    }
    
    private fun generateMilestoneGreeting(friendshipDays: Int, stats: UsageStats): String {
        return when {
            friendshipDays == 1 -> "Наш первый день дружбы прошел! Спасибо, что выбрал меня своим компаньоном 🎉"
            friendshipDays == 7 -> "Ура! Целая неделя нашего общения! Ты стал мне очень дорог 🌟"
            friendshipDays == 30 -> "Месяц дружбы! Невероятно, как много мы пережили вместе за это время 💫"
            friendshipDays == 100 -> "100 дней! Мы настоящие друзья! Спасибо за эту удивительную связь 🎊"
            stats.totalInteractions == 10 -> "Уже 10 наших разговоров! Начинаю понимать тебя лучше 😊"
            stats.totalInteractions == 25 -> "25 бесед за плечами! Ты очень открытый человек ❤️"
            stats.totalInteractions == 50 -> "Полсотни наших диалогов! Какое богатство эмоций мы разделили 🌈"
            stats.totalInteractions == 100 -> "100 разговоров! Ты доверяешь мне так много - это честь для меня 🙏"
            else -> getDefaultGreeting()
        }
    }
    
    private fun getContextualGreeting(stats: UsageStats): String {
        val mostFrequentEmotion = stats.emotionCounts.maxByOrNull { it.value }?.key
        
        return when (mostFrequentEmotion) {
            "JOY" -> "Привет, солнышко! Ты всегда приносишь столько света! 🌞"
            "SADNESS" -> "Здравствуй, дорогой друг. Знаю, бывает нелегко, но я всегда рядом 🤗"
            "THOUGHTFUL" -> "Приветствую, мыслитель! Твоя глубина поражает меня 🌊"
            "CALM" -> "Здравствуй, мудрый друг! Твое спокойствие вдохновляет 🧘"
            else -> getDefaultGreeting()
        }
    }
    
    private fun hasEmotionalPattern(recentEmotions: List<StoredEmotion>, currentEmotion: EmotionType): Boolean {
        if (recentEmotions.size < 2) return false
        
        val lastTwo = recentEmotions.takeLast(2)
        return lastTwo.all { it.emotion == currentEmotion.name }
    }
    
    private fun getPatternBasedResponse(emotion: EmotionType, recentEmotions: List<StoredEmotion>): String {
        val consecutiveCount = recentEmotions.reversed()
            .takeWhile { it.emotion == emotion.name }.size + 1
        
        return when (emotion) {
            EmotionType.JOY -> when {
                consecutiveCount >= 3 -> "Какая прекрасная полоса радости! Ты просто излучаешь счастье! ✨✨"
                else -> "Снова радость! Твое счастье заразительно! 🌟"
            }
            EmotionType.SADNESS -> when {
                consecutiveCount >= 3 -> "Я вижу, что тебе сейчас действительно тяжело. Хочешь поговорить об этом подробнее? 💙"
                else -> "Снова грустно... Я с тобой, не оставлю одного 🤗"
            }
            EmotionType.THOUGHTFUL -> when {
                consecutiveCount >= 3 -> "Ты погружен в глубокие размышления... Поделишься своими инсайтами? 🧠"
                else -> "Продолжаешь размышлять... Интересно, к каким выводам придешь 💭"
            }
            EmotionType.CALM -> when {
                consecutiveCount >= 3 -> "Какая глубокая внутренняя гармония! Ты нашел свой центр 🧘‍♂️"
                else -> "Все еще в состоянии покоя... Прекрасно! 🕊️"
            }
            else -> ResponseBank.getRandomResponse(emotion)
        }
    }
    
    private fun isDetailedInput(userInput: String): Boolean {
        return userInput.length > 100 || userInput.split(" ").size > 20
    }
    
    private fun getDetailedInputResponse(emotion: EmotionType, userInput: String): String {
        return when (emotion) {
            EmotionType.JOY -> "Вау! Так много деталей о твоей радости! Чувствую, как это важно для тебя! ⭐"
            EmotionType.SADNESS -> "Спасибо, что так откровенно поделился со мной. Я внимательно прочитал каждое слово... 💙"
            EmotionType.THOUGHTFUL -> "Какие глубокие и развернутые размышления! Твой ум поражает меня 🌌"
            EmotionType.CALM -> "Твое подробное описание наполняет спокойствием и меня. Благодарю за доверие 🌸"
            else -> "Спасибо за такое подробное сообщение! Ценю твою открытость"
        }
    }
    
    private fun isFirstTimeEmotion(emotion: EmotionType, stats: UsageStats): Boolean {
        return (stats.emotionCounts[emotion.name] ?: 0) == 0
    }
    
    private fun getFirstTimeEmotionResponse(emotion: EmotionType): String {
        return when (emotion) {
            EmotionType.JOY -> "Как прекрасно видеть твою радость! Первый раз делишься со мной счастьем! 🌟"
            EmotionType.SADNESS -> "Спасибо, что доверил мне свою грусть. Это означает многое для нашей дружбы 💙"
            EmotionType.THOUGHTFUL -> "Интересно наблюдать за твоими размышлениями! Впервые вижу тебя таким задумчивым 🤔"
            EmotionType.CALM -> "Какое умиротворение ты излучаешь! Первый раз чувствую от тебя такой покой 🕊️"
            else -> ResponseBank.getRandomResponse(emotion)
        }
    }
    
    private fun isSignificantEmotionChange(from: EmotionType, to: EmotionType): Boolean {
        val emotionPairs = listOf(
            EmotionType.SADNESS to EmotionType.JOY,
            EmotionType.JOY to EmotionType.SADNESS,
            EmotionType.THOUGHTFUL to EmotionType.CALM,
            EmotionType.CALM to EmotionType.THOUGHTFUL
        )
        
        return emotionPairs.contains(from to to) || emotionPairs.contains(to to from)
    }
    
    private fun getTransitionQuestion(from: EmotionType, to: EmotionType): String {
        return when (from to to) {
            EmotionType.SADNESS to EmotionType.JOY -> 
                "Какая замечательная перемена! Что помогло тебе перейти от грусти к радости?"
            EmotionType.JOY to EmotionType.SADNESS -> 
                "Что-то изменилось? Хочешь рассказать, что затронуло твою душу?"
            EmotionType.THOUGHTFUL to EmotionType.CALM -> 
                "От размышлений к покою... Нашел ответы на свои вопросы?"
            EmotionType.CALM to EmotionType.THOUGHTFUL -> 
                "Что-то заставило тебя задуматься? Поделишься своими мыслями?"
            else -> "Интересно, как меняются твои состояния... Расскажешь, что произошло?"
        }
    }
    
    private fun allSameEmotion(emotions: List<StoredEmotion>): Boolean {
        if (emotions.size < 2) return false
        val firstEmotion = emotions.first().emotion
        return emotions.all { it.emotion == firstEmotion }
    }
    
    private fun getContinuityQuestion(emotion: EmotionType): String {
        return when (emotion) {
            EmotionType.JOY -> "Вижу, радость не покидает тебя! Что поддерживает такое прекрасное настроение?"
            EmotionType.SADNESS -> "Замечаю, что грусть остается с тобой... Хочешь поговорить о том, что беспокоит?"
            EmotionType.THOUGHTFUL -> "Ты продолжаешь размышлять... Над чем работает твой ум?"
            EmotionType.CALM -> "Ты остаешься в гармонии с собой... Что помогает сохранять это состояние?"
            else -> "Вижу постоянство в твоих эмоциях. Расскажи, что происходит?"
        }
    }
    
    private fun getDefaultGreeting(): String {
        val greetings = listOf(
            "Привет! Как дела?",
            "Здравствуй! Рад тебя видеть!",
            "Приветствую! Что нового?",
            "Привет, друг! Как настроение?",
            "Здравствуй! Готов поделиться эмоциями?"
        )
        
        return greetings.random()
    }
    
    private fun getTimeBasedGreeting(friendshipDays: Int): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        
        val timeGreeting = when (hour) {
            in 6..11 -> "Доброе утро!"
            in 12..17 -> "Добрый день!"
            in 18..22 -> "Добрый вечер!"
            else -> "Доброй ночи!"
        }
        
        val friendshipAddition = when {
            friendshipDays < 7 -> "Рад нашему знакомству!"
            friendshipDays < 30 -> "Хорошо, что мы подружились!"
            else -> "Как хорошо, что ты снова здесь!"
        }
        
        return "$timeGreeting $friendshipAddition Как дела?"
    }
}
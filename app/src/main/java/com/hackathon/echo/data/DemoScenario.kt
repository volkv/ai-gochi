package com.hackathon.echo.data

data class DemoStep(
    val action: String,
    val inputText: String?,
    val expectedEmotion: EmotionType,
    val duration: Int,
    val presenterNote: String
)

object DemoScenario {
    val steps = listOf(
        DemoStep(
            action = "intro",
            inputText = null,
            expectedEmotion = EmotionType.NEUTRAL,
            duration = 30,
            presenterNote = "Знакомство с Эхо - покажите главный экран, объясните концепцию"
        ),
        DemoStep(
            action = "joy_demo",
            inputText = "Сегодня защитил диплом на отлично!",
            expectedEmotion = EmotionType.JOY,
            duration = 75,
            presenterNote = "Демонстрация радости - покажите анимации, золотые искорки, солнечную погоду"
        ),
        DemoStep(
            action = "sadness_demo",
            inputText = "Очень переживаю перед важной встречей...",
            expectedEmotion = EmotionType.SADNESS,
            duration = 60,
            presenterNote = "Демонстрация грусти - обратите внимание на синие цвета, тучи, вялое растение"
        ),
        DemoStep(
            action = "thoughtful_demo",
            inputText = "Размышляю о своем будущем и жизненном пути",
            expectedEmotion = EmotionType.THOUGHTFUL,
            duration = 65,
            presenterNote = "Демонстрация размышлений - фиолетовая аура, медитативная поза, свеча"
        ),
        DemoStep(
            action = "neutral_demo",
            inputText = "Обычный день, ничего особенного",
            expectedEmotion = EmotionType.NEUTRAL,
            duration = 55,
            presenterNote = "Демонстрация нейтрального состояния - спокойные тона, умиротворенность"
        ),
        DemoStep(
            action = "transition_demo",
            inputText = "Получил предложение о работе мечты!",
            expectedEmotion = EmotionType.JOY,
            duration = 45,
            presenterNote = "Показать плавные переходы между состояниями"
        )
    )
    
    val totalDemoTime = steps.sumOf { it.duration }
    
    fun getStepByIndex(index: Int): DemoStep? {
        return steps.getOrNull(index)
    }
    
    fun getCurrentStepNote(currentIndex: Int): String {
        return steps.getOrNull(currentIndex)?.presenterNote ?: "Демо завершено"
    }
}

data class DemoMessagePair(
    val userMessage: String,
    val petResponse: String
)

object DemoScriptedPhrases {
    val joyPairs = listOf(
        DemoMessagePair(
            "Сегодня защитил диплом на отлично!",
            "Поздравляю! Твой успех вдохновляет! 🌟"
        ),
        DemoMessagePair(
            "Получил предложение о работе мечты!",
            "Это потрясающе! Разделяю твое счастье! ✨"
        ),
        DemoMessagePair(
            "Встретил старого друга, было здорово!",
            "Какой прекрасный день для радости! 🎉"
        ),
        DemoMessagePair(
            "Выиграл в лотерею небольшую сумму!",
            "Ура! Давай отпразднуем вместе!"
        )
    )
    
    val sadnessPairs = listOf(
        DemoMessagePair(
            "Очень переживаю перед важной встречей...",
            "Я здесь, чтобы тебя поддержать 💙"
        ),
        DemoMessagePair(
            "Поссорился с близким человеком",
            "Понимаю, что тебе сейчас тяжело"
        ),
        DemoMessagePair(
            "Чувствую себя потерянным и уставшим",
            "Я рядом. Ты не один в этом 💙"
        ),
        DemoMessagePair(
            "Не получил работу, на которую очень рассчитывал",
            "Хочешь поговорить об этом? Я слушаю"
        )
    )
    
    val thoughtfulPairs = listOf(
        DemoMessagePair(
            "Размышляю о своем будущем и жизненном пути",
            "Размышления помогают нам расти 🤔"
        ),
        DemoMessagePair(
            "Думаю о смысле происходящих событий",
            "Глубокие мысли ведут к мудрости"
        ),
        DemoMessagePair(
            "Анализирую свои поступки и их последствия",
            "Философские размышления - пища для души"
        ),
        DemoMessagePair(
            "Пытаюсь понять, правильный ли выбрал путь",
            "Давай вместе поразмыслим над этим 🧠"
        )
    )
    
    val neutralPairs = listOf(
        DemoMessagePair(
            "Привет, как дела?",
            "Привет! Расскажи, что у тебя нового?"
        ),
        DemoMessagePair(
            "Что нового происходит?",
            "Я готов выслушать любые твои мысли"
        ),
        DemoMessagePair(
            "Обычный день, ничего особенного",
            "Как прошел твой день?"
        ),
        DemoMessagePair(
            "Просто хочется пообщаться",
            "Что у тебя на душе?"
        )
    )
    
    val joyPhrases = joyPairs.map { it.userMessage }
    val sadnessPhrases = sadnessPairs.map { it.userMessage }
    val thoughtfulPhrases = thoughtfulPairs.map { it.userMessage }
    val neutralPhrases = neutralPairs.map { it.userMessage }
    
    fun getPhrasesByEmotion(emotion: EmotionType): List<String> {
        return when (emotion) {
            EmotionType.JOY -> joyPhrases
            EmotionType.SADNESS -> sadnessPhrases
            EmotionType.THOUGHTFUL -> thoughtfulPhrases
            EmotionType.NEUTRAL -> neutralPhrases
        }
    }
    
    val demoCycleEmotions = listOf(
        EmotionType.JOY,
        EmotionType.SADNESS,
        EmotionType.THOUGHTFUL,
        EmotionType.NEUTRAL
    )
    
    fun getPairsByEmotion(emotion: EmotionType): List<DemoMessagePair> {
        return when (emotion) {
            EmotionType.JOY -> joyPairs
            EmotionType.SADNESS -> sadnessPairs
            EmotionType.THOUGHTFUL -> thoughtfulPairs
            EmotionType.NEUTRAL -> neutralPairs
        }
    }
    
    fun getDemoMessagePairByCycle(step: Int): DemoMessagePair {
        val emotionIndex = step % demoCycleEmotions.size
        val emotion = demoCycleEmotions[emotionIndex]
        val pairs = getPairsByEmotion(emotion)
        val pairIndex = (step / demoCycleEmotions.size) % pairs.size
        return pairs[pairIndex]
    }
    
    fun getDemoPhraseByCycle(step: Int): String {
        val emotionIndex = step % demoCycleEmotions.size
        val emotion = demoCycleEmotions[emotionIndex]
        return getRandomPhrase(emotion)
    }
    
    fun getDemoEmotionByCycle(step: Int): EmotionType {
        return demoCycleEmotions[step % demoCycleEmotions.size]
    }
    
    fun getRandomPhrase(emotion: EmotionType): String {
        return getPhrasesByEmotion(emotion).random()
    }
    
    fun getScriptedResponseForMessage(userMessage: String, emotion: EmotionType): String? {
        val pairs = getPairsByEmotion(emotion)
        return pairs.find { it.userMessage == userMessage }?.petResponse
    }
}

object PresentationGuide {
    val segments = mapOf(
        "opening" to "Знакомство с AI Тамагочи 'Эхо' (30 сек)",
        "joy_demo" to "Демонстрация радостного состояния (1:15)",
        "sadness_demo" to "Демонстрация грустного состояния (1:00)",
        "thoughtful_demo" to "Демонстрация задумчивого состояния (1:05)",
        "calm_demo" to "Демонстрация спокойного состояния (55 сек)",
        "transitions" to "Показ плавных переходов между состояниями (45 сек)",
        "closing" to "Заключение и вопросы жюри (остальное время)"
    )
    
    val keyPointsForJury = listOf(
        "Эхо реагирует на эмоциональный контекст сообщений",
        "Визуальные элементы синхронно меняются с состоянием питомца",
        "Анимации создают живой и отзывчивый интерфейс",
        "Приложение помогает пользователям выражать и обрабатывать эмоции",
        "Каждое состояние имеет уникальный визуальный язык"
    )
    
    val fallbackPlan = listOf(
        "Если приложение зависло - перезапустить",
        "Если звук не работает - объяснить словами",
        "Если анимации тормозят - показать на другом устройстве",
        "Если текстовый ввод глючит - использовать кнопки эмоций",
        "Последний план - видео с экрана как backup"
    )
    
    val deviceCheckList = listOf(
        "APK установлен на основном демо-устройстве",
        "Звук настроен на комфортный уровень (50-60%)",
        "Яркость экрана установлена на максимум",
        "Заготовленные фразы выписаны на бумаге",
        "Запасное устройство с установленным APK готово",
        "Видео-backup готово к воспроизведению при необходимости",
        "Зарядка устройств проверена (минимум 70%)",
        "Режим 'Не беспокоить' включен на всех устройствах"
    )
    
    fun getTimingForStep(stepIndex: Int): String {
        val step = DemoScenario.steps.getOrNull(stepIndex)
        return if (step != null) {
            "${step.duration} секунд на ${step.presenterNote}"
        } else {
            "Демо завершено"
        }
    }
    
    fun getTotalTimeRemaining(currentStep: Int): Int {
        return DemoScenario.steps.drop(currentStep).sumOf { it.duration }
    }
}
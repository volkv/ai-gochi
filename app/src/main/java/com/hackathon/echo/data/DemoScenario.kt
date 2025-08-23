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

object DemoScriptedPhrases {
    val joyPhrases = listOf(
        "Сегодня защитил диплом на отлично!",
        "Получил предложение о работе мечты!",
        "Встретил старого друга, было здорово!",
        "Выиграл в лотерею небольшую сумму!",
        "Сдал сложный экзамен лучше, чем ожидал!",
        "Получил комплимент от незнакомца",
        "Завершил долгосрочный проект успешно!",
        "Нашел идеальную квартиру для аренды!"
    )
    
    val sadnessPhrases = listOf(
        "Очень переживаю перед важной встречей...",
        "Поссорился с близким человеком",
        "Чувствую себя потерянным и уставшим",
        "Не получил работу, на которую очень рассчитывал",
        "Плохо себя чувствую из-за стресса",
        "Расстался с любимым человеком",
        "Получил плохие новости о здоровье родственника",
        "Чувствую одиночество в большом городе"
    )
    
    val thoughtfulPhrases = listOf(
        "Размышляю о своем будущем и жизненном пути",
        "Думаю о смысле происходящих событий",
        "Анализирую свои поступки и их последствия",
        "Пытаюсь понять, правильный ли выбрал путь",
        "Философствую о природе человеческих отношений",
        "Размышляю над книгой, которую недавно прочитал",
        "Думаю о том, как изменить свою жизнь к лучшему",
        "Погружен в мысли о вечных вопросах бытия"
    )
    
    val neutralPhrases = listOf(
        "Привет, как дела?",
        "Что нового происходит?",
        "Расскажи о своем дне",
        "Хочется поговорить с кем-то",
        "Обычный день, ничего особенного",
        "Думаю, что бы поделать",
        "Немного скучно сегодня",
        "Просто хочется пообщаться"
    )
    
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
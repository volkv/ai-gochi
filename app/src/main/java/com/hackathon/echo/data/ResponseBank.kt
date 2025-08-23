package com.hackathon.echo.data

object ResponseBank {
    val joyResponses = listOf(
        "Твоя радость заряжает меня энергией! ✨",
        "Как здорово! Я счастлив за тебя!",
        "Поздравляю! Твой успех вдохновляет!",
        "Это потрясающе! Разделяю твое счастье! 🌟",
        "Прекрасные новости! Ты просто молодец!",
        "Твоё ликование согревает мое сердечко! ☀️",
        "Ура! Давай отпразднуем вместе!",
        "Какой прекрасный день для радости! 🎉"
    )
    
    val sadnessResponses = listOf(
        "Я здесь, чтобы тебя поддержать 💙",
        "Понимаю, что тебе сейчас тяжело",
        "Твои чувства важны. Выговорись",
        "Я рядом. Ты не один в этом 💙",
        "Позволь себе погрустить, это нормально",
        "Вместе мы справимся с трудностями",
        "Хочешь поговорить об этом? Я слушаю",
        "Даже в грусти есть своя красота... 💧"
    )
    
    val thoughtfulResponses = listOf(
        "Размышления помогают нам расти 🤔",
        "Глубокие мысли ведут к мудрости",
        "Интересно, к чему приведут эти размышления...",
        "Философские размышления - пища для души",
        "Давай вместе поразмыслим над этим 🧠",
        "В тишине мыслей рождаются открытия",
        "Твоя мудрость впечатляет меня! ✨",
        "Размышления делают нас глубже и мудрее"
    )
    
    val calmResponses = listOf(
        "В тишине мы находим покой 🍃",
        "Какое умиротворение... Я тоже расслабляюсь",
        "Твоё спокойствие передается и мне 🧘",
        "Благодарность за эти моменты тишины",
        "Как приятно просто быть здесь и сейчас",
        "В покое есть своя особенная магия ☮️",
        "Медитативное состояние очень целебно",
        "Наслаждаемся моментом вместе... 🌙"
    )
    

    
    val neutralResponses = listOf(
        "Привет! Как дела?",
        "Расскажи, что у тебя нового?",
        "Я готов выслушать любые твои мысли",
        "Что тебя сегодня беспокоит?",
        "Делись со мной своими переживаниями",
        "Я здесь и готов поговорить о чём угодно",
        "Как прошел твой день?",
        "Что у тебя на душе?"
    )
    
    fun getRandomResponse(emotion: EmotionType): String {
        val responses = when (emotion) {
            EmotionType.JOY -> joyResponses
            EmotionType.SADNESS -> sadnessResponses
            EmotionType.THOUGHTFUL -> thoughtfulResponses
            EmotionType.NEUTRAL -> neutralResponses
        }
        return responses.random()
    }
    
    fun getResponseByIndex(emotion: EmotionType, index: Int): String {
        val responses = when (emotion) {
            EmotionType.JOY -> joyResponses
            EmotionType.SADNESS -> sadnessResponses
            EmotionType.THOUGHTFUL -> thoughtfulResponses
            EmotionType.NEUTRAL -> neutralResponses
        }
        return responses.getOrElse(index) { responses.random() }
    }
    
    fun getAllResponses(emotion: EmotionType): List<String> {
        return when (emotion) {
            EmotionType.JOY -> joyResponses
            EmotionType.SADNESS -> sadnessResponses
            EmotionType.THOUGHTFUL -> thoughtfulResponses
            EmotionType.NEUTRAL -> neutralResponses
        }
    }
}
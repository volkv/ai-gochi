import com.hackathon.echo.data.*

fun testDemoScenario() {
    println("=== ТЕСТИРОВАНИЕ ДЕМО-СЦЕНАРИЯ ===\n")
    
    val demoSteps = DemoScenario.steps
    
    demoSteps.forEachIndexed { index, step ->
        println("Шаг ${index + 1}: ${step.action}")
        println("Время: ${step.duration} сек")
        println("Примечание: ${step.presenterNote}")
        
        if (step.inputText != null) {
            println("Тестовая фраза: \"${step.inputText}\"")
            println("Ожидаемая эмоция: ${step.expectedEmotion}")
            
            val detectedEmotion = simulateEmotionDetection(step.inputText)
            val isCorrect = detectedEmotion == step.expectedEmotion
            
            println("Распознанная эмоция: $detectedEmotion")
            println("Результат: ${if (isCorrect) "✅ КОРРЕКТНО" else "❌ ОШИБКА"}")
        } else {
            println("Без текстового ввода")
        }
        
        println("─".repeat(50))
    }
    
    println("\n=== ТЕСТИРОВАНИЕ ЗАГОТОВЛЕННЫХ ФРАЗ ===\n")
    
    testPhrasesForEmotion("JOY", EmotionType.JOY, DemoScriptedPhrases.joyPhrases)
    testPhrasesForEmotion("SADNESS", EmotionType.SADNESS, DemoScriptedPhrases.sadnessPhrases)
    testPhrasesForEmotion("THOUGHTFUL", EmotionType.THOUGHTFUL, DemoScriptedPhrases.thoughtfulPhrases)
    testPhrasesForEmotion("CALM", EmotionType.CALM, DemoScriptedPhrases.calmPhrases)
    testPhrasesForEmotion("NEUTRAL", EmotionType.NEUTRAL, DemoScriptedPhrases.neutralPhrases)
    
    println("\n=== ИТОГОВОЕ ВРЕМЯ ДЕМО ===")
    println("Общее время: ${DemoScenario.totalDemoTime} секунд (${DemoScenario.totalDemoTime / 60.0} минут)")
}

fun testPhrasesForEmotion(emotionName: String, expectedEmotion: EmotionType, phrases: List<String>) {
    println("Тестирование фраз для $emotionName:")
    
    var correctCount = 0
    phrases.forEachIndexed { index, phrase ->
        val detected = simulateEmotionDetection(phrase)
        val isCorrect = detected == expectedEmotion
        if (isCorrect) correctCount++
        
        println("  ${index + 1}. \"$phrase\"")
        println("     Распознано: $detected ${if (isCorrect) "✅" else "❌"}")
    }
    
    val accuracy = (correctCount.toDouble() / phrases.size * 100).toInt()
    println("  Точность: $correctCount/${phrases.size} ($accuracy%)\n")
}

fun simulateEmotionDetection(text: String): EmotionType {
    val lowercaseText = text.lowercase()
    
    val joyKeywords = listOf(
        "радость", "счастлив", "отлично", "здорово", "супер", "прекрасно", 
        "восторг", "ура", "победа", "успех", "достижение", "поздравь", "праздник",
        "защитил", "диплом", "работе", "мечты", "встретил", "друга", "выиграл",
        "сдал", "экзамен", "комплимент", "завершил", "проект", "квартиру"
    )
    
    val sadnessKeywords = listOf(
        "грустно", "печально", "расстроен", "переживаю", "тревога", "проблема",
        "болит", "тяжело", "плохо", "устал", "депрессия", "одиноко", "страшно",
        "встречей", "поссорился", "потерянным", "уставшим", "получил", "стресса",
        "расстался", "одиночество"
    )
    
    val thoughtfulKeywords = listOf(
        "думаю", "размышляю", "интересно", "будущее", "философия", "смысл",
        "вопрос", "почему", "задаюсь", "мысли", "анализ", "понимание", "мудрость",
        "жизненном", "пути", "происходящих", "поступки", "последствия", "выбрал",
        "отношений", "книгой", "изменить", "бытия"
    )
    
    val calmKeywords = listOf(
        "спокойно", "тишина", "покой", "умиротворен", "благодарен", "медитация",
        "релакс", "дышу", "наслаждаюсь", "гармония", "баланс", "мир", "zen",
        "наслаждаюсь", "медитирую", "звуки", "природы", "расслабляюсь", "осознанность",
        "напряжение", "радости", "равновесия"
    )
    
    val joyScore = joyKeywords.count { lowercaseText.contains(it) }
    val sadnessScore = sadnessKeywords.count { lowercaseText.contains(it) }
    val thoughtfulScore = thoughtfulKeywords.count { lowercaseText.contains(it) }
    val calmScore = calmKeywords.count { lowercaseText.contains(it) }
    
    val maxScore = maxOf(joyScore, sadnessScore, thoughtfulScore, calmScore)
    
    return when (maxScore) {
        joyScore -> if (joyScore > 0) EmotionType.JOY else EmotionType.NEUTRAL
        sadnessScore -> if (sadnessScore > 0) EmotionType.SADNESS else EmotionType.NEUTRAL
        thoughtfulScore -> if (thoughtfulScore > 0) EmotionType.THOUGHTFUL else EmotionType.NEUTRAL
        calmScore -> if (calmScore > 0) EmotionType.CALM else EmotionType.NEUTRAL
        else -> EmotionType.NEUTRAL
    }
}
package com.hackathon.echo.data

import kotlinx.serialization.Serializable

@Serializable
data class PetStats(
    val joy: Int = 50,
    val sadness: Int = 50,
    val thoughtful: Int = 50,
    val neutral: Int = 50,
    val empathy: Int = 50
) {
    companion object {
        fun getDefault(): PetStats = PetStats()
        
        fun increase(stats: PetStats, emotion: EmotionType, amount: Int = 10): PetStats {
            return when (emotion) {
                EmotionType.JOY -> stats.copy(joy = (stats.joy + amount).coerceIn(0, 100))
                EmotionType.SADNESS -> stats.copy(sadness = (stats.sadness + amount).coerceIn(0, 100))
                EmotionType.THOUGHTFUL -> stats.copy(thoughtful = (stats.thoughtful + amount).coerceIn(0, 100))
                EmotionType.NEUTRAL -> stats.copy(neutral = (stats.neutral + amount).coerceIn(0, 100))
            }
        }
        
        fun decrease(stats: PetStats, emotion: EmotionType, amount: Int = 5): PetStats {
            return when (emotion) {
                EmotionType.JOY -> stats.copy(joy = (stats.joy - amount).coerceIn(0, 100))
                EmotionType.SADNESS -> stats.copy(sadness = (stats.sadness - amount).coerceIn(0, 100))
                EmotionType.THOUGHTFUL -> stats.copy(thoughtful = (stats.thoughtful - amount).coerceIn(0, 100))
                EmotionType.NEUTRAL -> stats.copy(neutral = (stats.neutral - amount).coerceIn(0, 100))
            }
        }
        
        fun increaseEmpathy(stats: PetStats, amount: Int = 5): PetStats {
            return stats.copy(empathy = (stats.empathy + amount).coerceIn(0, 100))
        }
    }
}
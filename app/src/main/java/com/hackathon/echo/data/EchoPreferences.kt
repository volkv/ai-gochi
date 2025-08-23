package com.hackathon.echo.data

import android.content.Context
import android.content.SharedPreferences
import com.hackathon.echo.viewmodel.InteractionHistory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class StoredEmotion(
    val emotion: String,
    val timestamp: Long,
    val userInput: String
)

@Serializable
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val unlockedTimestamp: Long,
    val isUnlocked: Boolean = false
)

@Serializable
data class UsageStats(
    val totalInteractions: Int = 0,
    val emotionCounts: Map<String, Int> = emptyMap(),
    val firstLaunch: Long = 0L,
    val lastInteraction: Long = 0L,
    val friendshipDays: Int = 0
)

class EchoPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }
    
    companion object {
        private const val PREFS_NAME = "echo_preferences"
        private const val KEY_FRIENDSHIP_DAYS = "friendship_days"
        private const val KEY_FIRST_LAUNCH_DATE = "first_launch_date"
        private const val KEY_LAST_INTERACTION_DATE = "last_interaction_date"
        private const val KEY_EMOTION_HISTORY = "emotion_history"
        private const val KEY_ACHIEVEMENTS = "achievements"
        private const val KEY_USAGE_STATS = "usage_stats"
        private const val KEY_PERSONALIZED_GREETINGS_ENABLED = "personalized_greetings"
        private const val KEY_TOTAL_INTERACTIONS = "total_interactions"
        
        private const val MAX_STORED_EMOTIONS = 20
    }
    
    fun getFriendshipDays(): Int {
        val firstLaunch = getFirstLaunchDate()
        val today = LocalDate.now()
        val daysSince = java.time.temporal.ChronoUnit.DAYS.between(firstLaunch, today).toInt()
        
        val storedDays = prefs.getInt(KEY_FRIENDSHIP_DAYS, daysSince)
        return maxOf(storedDays, daysSince)
    }
    
    fun updateFriendshipDays() {
        val days = getFriendshipDays()
        prefs.edit().putInt(KEY_FRIENDSHIP_DAYS, days).apply()
    }
    
    private fun getFirstLaunchDate(): LocalDate {
        val dateString = prefs.getString(KEY_FIRST_LAUNCH_DATE, null)
        return if (dateString != null) {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
        } else {
            val today = LocalDate.now()
            setFirstLaunchDate(today)
            today
        }
    }
    
    private fun setFirstLaunchDate(date: LocalDate) {
        prefs.edit()
            .putString(KEY_FIRST_LAUNCH_DATE, date.format(DateTimeFormatter.ISO_LOCAL_DATE))
            .apply()
    }
    
    fun getLastInteractionDate(): LocalDate? {
        val dateString = prefs.getString(KEY_LAST_INTERACTION_DATE, null)
        return dateString?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
    }
    
    fun updateLastInteractionDate() {
        val today = LocalDate.now()
        prefs.edit()
            .putString(KEY_LAST_INTERACTION_DATE, today.format(DateTimeFormatter.ISO_LOCAL_DATE))
            .apply()
    }
    
    fun saveEmotionToHistory(emotion: EmotionType, userInput: String) {
        val currentHistory = getEmotionHistory().toMutableList()
        val newEmotion = StoredEmotion(
            emotion = emotion.name,
            timestamp = System.currentTimeMillis(),
            userInput = userInput
        )
        
        currentHistory.add(newEmotion)
        
        val limitedHistory = if (currentHistory.size > MAX_STORED_EMOTIONS) {
            currentHistory.takeLast(MAX_STORED_EMOTIONS)
        } else {
            currentHistory
        }
        
        val jsonString = json.encodeToString(limitedHistory)
        prefs.edit().putString(KEY_EMOTION_HISTORY, jsonString).apply()
        
        updateLastInteractionDate()
    }
    
    fun getEmotionHistory(): List<StoredEmotion> {
        val jsonString = prefs.getString(KEY_EMOTION_HISTORY, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<StoredEmotion>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun getRecentEmotions(count: Int = 5): List<StoredEmotion> {
        return getEmotionHistory().takeLast(count)
    }
    
    fun saveAchievements(achievements: List<Achievement>) {
        val jsonString = json.encodeToString(achievements)
        prefs.edit().putString(KEY_ACHIEVEMENTS, jsonString).apply()
    }
    
    fun getAchievements(): List<Achievement> {
        val jsonString = prefs.getString(KEY_ACHIEVEMENTS, null) ?: return getDefaultAchievements()
        return try {
            json.decodeFromString<List<Achievement>>(jsonString)
        } catch (e: Exception) {
            getDefaultAchievements()
        }
    }
    
    fun unlockAchievement(achievementId: String): Boolean {
        val achievements = getAchievements().toMutableList()
        val achievementIndex = achievements.indexOfFirst { it.id == achievementId }
        
        if (achievementIndex != -1 && !achievements[achievementIndex].isUnlocked) {
            achievements[achievementIndex] = achievements[achievementIndex].copy(
                isUnlocked = true,
                unlockedTimestamp = System.currentTimeMillis()
            )
            saveAchievements(achievements)
            return true
        }
        
        return false
    }
    
    fun getUsageStats(): UsageStats {
        val jsonString = prefs.getString(KEY_USAGE_STATS, null) ?: return UsageStats(
            firstLaunch = getFirstLaunchDate().toEpochDay() * 24 * 60 * 60 * 1000,
            friendshipDays = getFriendshipDays()
        )
        
        return try {
            val stats = json.decodeFromString<UsageStats>(jsonString)
            stats.copy(friendshipDays = getFriendshipDays())
        } catch (e: Exception) {
            UsageStats(
                firstLaunch = getFirstLaunchDate().toEpochDay() * 24 * 60 * 60 * 1000,
                friendshipDays = getFriendshipDays()
            )
        }
    }
    
    fun updateUsageStats(emotionCounts: Map<EmotionType, Int>, totalInteractions: Int) {
        val emotionCountsString = emotionCounts.mapKeys { it.key.name }
        val stats = UsageStats(
            totalInteractions = totalInteractions,
            emotionCounts = emotionCountsString,
            firstLaunch = getFirstLaunchDate().toEpochDay() * 24 * 60 * 60 * 1000,
            lastInteraction = System.currentTimeMillis(),
            friendshipDays = getFriendshipDays()
        )
        
        val jsonString = json.encodeToString(stats)
        prefs.edit().putString(KEY_USAGE_STATS, jsonString).apply()
        
        checkAndUnlockAchievements(stats)
    }
    
    fun isPersonalizedGreetingsEnabled(): Boolean {
        return prefs.getBoolean(KEY_PERSONALIZED_GREETINGS_ENABLED, true)
    }
    
    fun setPersonalizedGreetingsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_PERSONALIZED_GREETINGS_ENABLED, enabled).apply()
    }
    
    private fun getDefaultAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                id = "first_friend",
                name = "Первый друг",
                description = "Познакомься с Эхо",
                unlockedTimestamp = 0L
            ),
            Achievement(
                id = "week_friendship",
                name = "Недельная дружба",
                description = "7 дней общения с Эхо",
                unlockedTimestamp = 0L
            ),
            Achievement(
                id = "emotion_explorer",
                name = "Исследователь эмоций",
                description = "Испытай все 4 основные эмоции",
                unlockedTimestamp = 0L
            ),
            Achievement(
                id = "chatty_friend",
                name = "Болтливый друг",
                description = "Проведи 50 взаимодействий",
                unlockedTimestamp = 0L
            ),
            Achievement(
                id = "deep_thinker",
                name = "Глубокий мыслитель",
                description = "10 размышлений подряд",
                unlockedTimestamp = 0L
            ),
            Achievement(
                id = "joy_spreader",
                name = "Распространитель радости",
                description = "20 радостных моментов",
                unlockedTimestamp = 0L
            )
        )
    }
    
    private fun checkAndUnlockAchievements(stats: UsageStats) {
        val achievements = getAchievements().toMutableList()
        var hasChanges = false
        
        achievements.forEachIndexed { index, achievement ->
            if (!achievement.isUnlocked) {
                val shouldUnlock = when (achievement.id) {
                    "first_friend" -> stats.totalInteractions >= 1
                    "week_friendship" -> stats.friendshipDays >= 7
                    "emotion_explorer" -> stats.emotionCounts.keys.size >= 4
                    "chatty_friend" -> stats.totalInteractions >= 50
                    "deep_thinker" -> (stats.emotionCounts["THOUGHTFUL"] ?: 0) >= 10
                    "joy_spreader" -> (stats.emotionCounts["JOY"] ?: 0) >= 20
                    else -> false
                }
                
                if (shouldUnlock) {
                    achievements[index] = achievement.copy(
                        isUnlocked = true,
                        unlockedTimestamp = System.currentTimeMillis()
                    )
                    hasChanges = true
                }
            }
        }
        
        if (hasChanges) {
            saveAchievements(achievements)
        }
    }
    
    fun clearAllData() {
        prefs.edit().clear().apply()
    }
    
    fun exportUserData(): String {
        val data = mapOf(
            "friendshipDays" to getFriendshipDays(),
            "emotionHistory" to getEmotionHistory(),
            "achievements" to getAchievements(),
            "usageStats" to getUsageStats()
        )
        
        return json.encodeToString(data)
    }
}
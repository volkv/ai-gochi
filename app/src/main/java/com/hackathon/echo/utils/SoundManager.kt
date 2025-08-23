package com.hackathon.echo.utils

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.hackathon.echo.R
import com.hackathon.echo.data.EmotionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class SoundType {
    JOY_CHIME,
    SAD_AMBIENT, 
    THOUGHTFUL_MEDITATION,
    CALM_NATURE,
    MESSAGE_SEND,
    MESSAGE_RECEIVE,
    NONE
}

data class SoundSettings(
    val isSoundEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true,
    val soundVolume: Float = 0.5f
)

data class VibrationPattern(
    val pattern: LongArray,
    val repeat: Int = -1
) {
    companion object {
        val JOY_PATTERN = VibrationPattern(
            pattern = longArrayOf(0, 150, 50, 150, 50, 150),
            repeat = -1
        )
        
        val SADNESS_PATTERN = VibrationPattern(
            pattern = longArrayOf(0, 800),
            repeat = -1
        )
        
        val THOUGHTFUL_PATTERN = VibrationPattern(
            pattern = longArrayOf(0, 300, 200, 300, 200, 300, 200, 300),
            repeat = -1
        )
        
        val CALM_PATTERN = VibrationPattern(
            pattern = longArrayOf(),
            repeat = -1
        )
        
        val NEUTRAL_PATTERN = VibrationPattern(
            pattern = longArrayOf(),
            repeat = -1
        )
    }
}

object SoundManager {
    private const val TAG = "SoundManager"
    private const val MAX_SOUND_DURATION = 10000L
    
    private var context: Context? = null
    private var mediaPlayers: MutableMap<SoundType, MediaPlayer> = mutableMapOf()
    private var vibrator: Vibrator? = null
    private var soundSettings = SoundSettings()
    private var currentPlayingJob: Job? = null
    
    fun initialize(context: Context) {
        this.context = context.applicationContext
        setupVibrator()
        preloadSounds()
    }
    
    private fun setupVibrator() {
        context?.let { ctx ->
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
        }
    }
    
    private fun preloadSounds() {
        try {
            // Создаем MediaPlayer объекты для каждого звука
            // В реальном проекте здесь будут настоящие звуковые файлы
            // Пока создаем пустые плейеры для демонстрации архитектуры
            
            SoundType.values().filter { it != SoundType.NONE }.forEach { soundType ->
                val resourceId = getSoundResourceId(soundType)
                if (resourceId != -1) {
                    try {
                        val mediaPlayer = MediaPlayer.create(context, resourceId)
                        mediaPlayer?.let { player ->
                            player.setVolume(soundSettings.soundVolume, soundSettings.soundVolume)
                            mediaPlayers[soundType] = player
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Could not preload sound for $soundType: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error preloading sounds: ${e.message}")
        }
    }
    
    private fun getSoundResourceId(soundType: SoundType): Int {
        return when (soundType) {
            SoundType.JOY_CHIME -> R.raw.joy_chime
            SoundType.SAD_AMBIENT -> R.raw.sad_ambient  
            SoundType.THOUGHTFUL_MEDITATION -> R.raw.thoughtful_meditation
            SoundType.CALM_NATURE -> R.raw.calm_nature
            SoundType.MESSAGE_SEND -> R.raw.message_send
            SoundType.MESSAGE_RECEIVE -> R.raw.message_receive
            SoundType.NONE -> -1
        }
    }
    
    fun playEmotionSound(emotion: EmotionType) {
        val soundType = getSoundTypeForEmotion(emotion)
        playSound(soundType)
        vibrateForEmotion(emotion)
    }
    
    fun playSound(soundType: SoundType) {
        if (!soundSettings.isSoundEnabled || soundType == SoundType.NONE) {
            return
        }
        
        currentPlayingJob?.cancel()
        
        currentPlayingJob = CoroutineScope(Dispatchers.Main).launch {
            try {
                val mediaPlayer = mediaPlayers[soundType]
                
                if (mediaPlayer == null) {
                    // Fallback: создаем новый плейер если преложенный не найден
                    val resourceId = getSoundResourceId(soundType)
                    if (resourceId != -1) {
                        val newPlayer = MediaPlayer.create(context, resourceId)
                        newPlayer?.let { player ->
                            player.setVolume(soundSettings.soundVolume, soundSettings.soundVolume)
                            player.start()
                            
                            delay(MAX_SOUND_DURATION)
                            player.stop()
                            player.release()
                        }
                    }
                } else {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                        mediaPlayer.prepare()
                    }
                    mediaPlayer.start()
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error playing sound $soundType: ${e.message}")
            }
        }
    }
    
    fun vibrateForEmotion(emotion: EmotionType) {
        if (!soundSettings.isVibrationEnabled) {
            return
        }
        
        val pattern = getVibrationPatternForEmotion(emotion)
        vibrate(pattern)
    }
    
    private fun vibrate(vibrationPattern: VibrationPattern) {
        if (vibrationPattern.pattern.isEmpty()) {
            return
        }
        
        vibrator?.let { vib ->
            if (vib.hasVibrator()) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val effect = VibrationEffect.createWaveform(
                            vibrationPattern.pattern,
                            vibrationPattern.repeat
                        )
                        vib.vibrate(effect)
                    } else {
                        @Suppress("DEPRECATION")
                        vib.vibrate(vibrationPattern.pattern, vibrationPattern.repeat)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error during vibration: ${e.message}")
                }
            }
        }
    }
    
    private fun getSoundTypeForEmotion(emotion: EmotionType): SoundType {
        return when (emotion) {
            EmotionType.JOY -> SoundType.JOY_CHIME
            EmotionType.SADNESS -> SoundType.SAD_AMBIENT
            EmotionType.THOUGHTFUL -> SoundType.THOUGHTFUL_MEDITATION

            EmotionType.NEUTRAL -> SoundType.NONE
        }
    }
    
    private fun getVibrationPatternForEmotion(emotion: EmotionType): VibrationPattern {
        return when (emotion) {
            EmotionType.JOY -> VibrationPattern.JOY_PATTERN
            EmotionType.SADNESS -> VibrationPattern.SADNESS_PATTERN
            EmotionType.THOUGHTFUL -> VibrationPattern.THOUGHTFUL_PATTERN

            EmotionType.NEUTRAL -> VibrationPattern.NEUTRAL_PATTERN
        }
    }
    
    fun updateSettings(newSettings: SoundSettings) {
        soundSettings = newSettings
        
        // Обновляем громкость для всех загруженных плейеров
        mediaPlayers.values.forEach { mediaPlayer ->
            try {
                mediaPlayer.setVolume(soundSettings.soundVolume, soundSettings.soundVolume)
            } catch (e: Exception) {
                Log.w(TAG, "Could not update volume for media player: ${e.message}")
            }
        }
    }
    
    fun stopAllSounds() {
        currentPlayingJob?.cancel()
        
        mediaPlayers.values.forEach { mediaPlayer ->
            try {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                    mediaPlayer.prepare()
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error stopping media player: ${e.message}")
            }
        }
    }
    
    fun release() {
        stopAllSounds()
        
        mediaPlayers.values.forEach { mediaPlayer ->
            try {
                mediaPlayer.release()
            } catch (e: Exception) {
                Log.w(TAG, "Error releasing media player: ${e.message}")
            }
        }
        
        mediaPlayers.clear()
        vibrator = null
        context = null
    }
    
    fun getCurrentSettings(): SoundSettings = soundSettings
    
    fun isSoundAvailable(soundType: SoundType): Boolean {
        return mediaPlayers.containsKey(soundType) && 
               getSoundResourceId(soundType) != -1
    }
    
    fun testVibration(emotion: EmotionType) {
        if (soundSettings.isVibrationEnabled) {
            vibrateForEmotion(emotion)
        }
    }
}
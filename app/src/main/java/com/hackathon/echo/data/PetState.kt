package com.hackathon.echo.data

import androidx.compose.ui.graphics.Color

enum class EmotionType {
    JOY,
    SADNESS,
    THOUGHTFUL,
    CALM,
    NEUTRAL
}

enum class RoomLighting {
    WARM,
    COLD,
    SOFT,
    BALANCED
}

enum class PlantState {
    BLOOMING,
    WILTING,
    NORMAL
}

enum class WeatherState {
    SUNNY,
    CLOUDY,
    CLEAR
}

data class PetState(
    val emotion: EmotionType,
    val color: Color,
    val expression: String,
    val roomLighting: RoomLighting,
    val plantState: PlantState,
    val weatherState: WeatherState
)

object PetStates {
    val joyState = PetState(
        emotion = EmotionType.JOY,
        color = Color(0xFFFFD700),
        expression = "широкая улыбка, блестящие глаза",
        roomLighting = RoomLighting.WARM,
        plantState = PlantState.BLOOMING,
        weatherState = WeatherState.SUNNY
    )
    
    val sadnessState = PetState(
        emotion = EmotionType.SADNESS,
        color = Color(0xFF4169E1),
        expression = "опущенные глаза, маленькая слезинка",
        roomLighting = RoomLighting.COLD,
        plantState = PlantState.WILTING,
        weatherState = WeatherState.CLOUDY
    )
    
    val thoughtfulState = PetState(
        emotion = EmotionType.THOUGHTFUL,
        color = Color(0xFF9370DB),
        expression = "закрытые глаза, спокойное лицо",
        roomLighting = RoomLighting.SOFT,
        plantState = PlantState.NORMAL,
        weatherState = WeatherState.CLEAR
    )
    
    val calmState = PetState(
        emotion = EmotionType.CALM,
        color = Color(0xFF32CD32),
        expression = "умиротворенная полуулыбка",
        roomLighting = RoomLighting.BALANCED,
        plantState = PlantState.NORMAL,
        weatherState = WeatherState.CLEAR
    )
    
    val neutralState = PetState(
        emotion = EmotionType.NEUTRAL,
        color = Color(0xFFCCCCCC),
        expression = "спокойное лицо",
        roomLighting = RoomLighting.BALANCED,
        plantState = PlantState.NORMAL,
        weatherState = WeatherState.CLEAR
    )
    
    fun getStateByEmotion(emotion: EmotionType): PetState {
        return when (emotion) {
            EmotionType.JOY -> joyState
            EmotionType.SADNESS -> sadnessState
            EmotionType.THOUGHTFUL -> thoughtfulState
            EmotionType.CALM -> calmState
            EmotionType.NEUTRAL -> neutralState
        }
    }
}
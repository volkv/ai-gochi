package com.hackathon.echo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

enum class WeatherState {
    SUNNY, CLOUDY
}

enum class PlantState {
    BLOOMING, NORMAL, WITHERING
}

enum class RoomLighting {
    WARM, COOL, NEUTRAL
}

data class RoomState(
    val weather: WeatherState = WeatherState.SUNNY,
    val plant: PlantState = PlantState.NORMAL,
    val lighting: RoomLighting = RoomLighting.NEUTRAL,
    val hasCandle: Boolean = false
)

@Composable
fun RoomBackground(
    roomState: RoomState,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = when (roomState.lighting) {
                    RoomLighting.WARM -> Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFF8DC), Color(0xFFFFE4B5))
                    )
                    RoomLighting.COOL -> Brush.verticalGradient(
                        colors = listOf(Color(0xFFE6F3FF), Color(0xFFB3D9FF))
                    )
                    RoomLighting.NEUTRAL -> Brush.verticalGradient(
                        colors = listOf(Color(0xFFF5F5F5), Color(0xFFE0E0E0))
                    )
                }
            )
    ) {
        drawRoom(roomState)
    }
}

private fun DrawScope.drawRoom(roomState: RoomState) {
    val width = size.width
    val height = size.height
    
    drawFloor(width, height)
    drawWindow(roomState.weather, width, height)
    drawTable(width, height)
    drawPlant(roomState.plant, width, height)
    
    if (roomState.hasCandle) {
        drawCandle(width, height)
    }
}

private fun DrawScope.drawFloor(width: Float, height: Float) {
    drawRect(
        color = Color(0xFFD2B48C),
        topLeft = Offset(0f, height * 0.8f),
        size = Size(width, height * 0.2f)
    )
    
    for (i in 1..5) {
        val y = height * 0.8f + (height * 0.2f * i / 6)
        drawLine(
            color = Color(0xFFC19A6B),
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 2f
        )
    }
}

private fun DrawScope.drawWindow(weather: WeatherState, width: Float, height: Float) {
    val windowWidth = width * 0.3f
    val windowHeight = height * 0.25f
    val windowLeft = width * 0.1f
    val windowTop = height * 0.1f
    
    drawRect(
        color = Color(0xFF8B4513),
        topLeft = Offset(windowLeft - 10f, windowTop - 10f),
        size = Size(windowWidth + 20f, windowHeight + 20f)
    )
    
    val skyColor = when (weather) {
        WeatherState.SUNNY -> Color(0xFF87CEEB)
        WeatherState.CLOUDY -> Color(0xFF708090)
    }
    
    drawRect(
        color = skyColor,
        topLeft = Offset(windowLeft, windowTop),
        size = Size(windowWidth, windowHeight)
    )
    
    when (weather) {
        WeatherState.SUNNY -> {
            val sunRadius = windowWidth * 0.15f
            val sunCenter = Offset(
                windowLeft + windowWidth * 0.7f,
                windowTop + windowHeight * 0.3f
            )
            drawCircle(
                color = Color(0xFFFFD700),
                radius = sunRadius,
                center = sunCenter
            )
            
            for (i in 0..7) {
                val angle = i * 45f * Math.PI / 180f
                val rayStart = Offset(
                    sunCenter.x + Math.cos(angle).toFloat() * (sunRadius + 5f),
                    sunCenter.y + Math.sin(angle).toFloat() * (sunRadius + 5f)
                )
                val rayEnd = Offset(
                    sunCenter.x + Math.cos(angle).toFloat() * (sunRadius + 15f),
                    sunCenter.y + Math.sin(angle).toFloat() * (sunRadius + 15f)
                )
                drawLine(
                    color = Color(0xFFFFD700),
                    start = rayStart,
                    end = rayEnd,
                    strokeWidth = 3f
                )
            }
        }
        WeatherState.CLOUDY -> {
            drawCloud(
                Offset(windowLeft + windowWidth * 0.3f, windowTop + windowHeight * 0.4f),
                windowWidth * 0.15f
            )
            drawCloud(
                Offset(windowLeft + windowWidth * 0.7f, windowTop + windowHeight * 0.2f),
                windowWidth * 0.12f
            )
        }
    }
    
    drawLine(
        color = Color(0xFF654321),
        start = Offset(windowLeft + windowWidth / 2, windowTop),
        end = Offset(windowLeft + windowWidth / 2, windowTop + windowHeight),
        strokeWidth = 4f
    )
    drawLine(
        color = Color(0xFF654321),
        start = Offset(windowLeft, windowTop + windowHeight / 2),
        end = Offset(windowLeft + windowWidth, windowTop + windowHeight / 2),
        strokeWidth = 4f
    )
}

private fun DrawScope.drawCloud(center: Offset, radius: Float) {
    val cloudColor = Color(0xFFFFFFFF)
    drawCircle(cloudColor, radius * 0.8f, center)
    drawCircle(cloudColor, radius * 0.6f, Offset(center.x - radius * 0.4f, center.y))
    drawCircle(cloudColor, radius * 0.6f, Offset(center.x + radius * 0.4f, center.y))
    drawCircle(cloudColor, radius * 0.5f, Offset(center.x - radius * 0.2f, center.y - radius * 0.3f))
    drawCircle(cloudColor, radius * 0.5f, Offset(center.x + radius * 0.2f, center.y - radius * 0.3f))
}

private fun DrawScope.drawTable(width: Float, height: Float) {
    val tableWidth = width * 0.4f
    val tableHeight = height * 0.1f
    val tableLeft = width * 0.55f
    val tableTop = height * 0.65f
    
    drawRect(
        color = Color(0xFF8B4513),
        topLeft = Offset(tableLeft, tableTop),
        size = Size(tableWidth, tableHeight)
    )
    
    val legWidth = tableWidth * 0.05f
    val legHeight = height * 0.15f
    
    for (i in 0..1) {
        for (j in 0..1) {
            val legX = tableLeft + i * (tableWidth - legWidth)
            val legY = tableTop + tableHeight
            drawRect(
                color = Color(0xFF654321),
                topLeft = Offset(legX, legY),
                size = Size(legWidth, legHeight)
            )
        }
    }
}

private fun DrawScope.drawPlant(plantState: PlantState, width: Float, height: Float) {
    val potWidth = width * 0.12f
    val potHeight = height * 0.08f
    val potLeft = width * 0.6f
    val potTop = height * 0.57f
    
    drawRect(
        color = Color(0xFF8B4513),
        topLeft = Offset(potLeft, potTop),
        size = Size(potWidth, potHeight)
    )
    
    val stemX = potLeft + potWidth / 2
    val stemTop = potTop - height * 0.15f
    val stemHeight = height * 0.15f
    
    drawLine(
        color = Color(0xFF228B22),
        start = Offset(stemX, potTop),
        end = Offset(stemX, stemTop),
        strokeWidth = 4f
    )
    
    when (plantState) {
        PlantState.BLOOMING -> {
            for (i in 0..4) {
                val angle = i * 72f * Math.PI / 180f
                val petalEnd = Offset(
                    stemX + Math.cos(angle).toFloat() * 15f,
                    stemTop + Math.sin(angle).toFloat() * 15f
                )
                drawCircle(
                    color = Color(0xFFFF69B4),
                    radius = 8f,
                    center = petalEnd
                )
            }
            drawCircle(
                color = Color(0xFFFFD700),
                radius = 6f,
                center = Offset(stemX, stemTop)
            )
        }
        PlantState.NORMAL -> {
            drawCircle(
                color = Color(0xFF90EE90),
                radius = 12f,
                center = Offset(stemX, stemTop)
            )
            drawCircle(
                color = Color(0xFF90EE90),
                radius = 8f,
                center = Offset(stemX - 10f, stemTop + 5f)
            )
            drawCircle(
                color = Color(0xFF90EE90),
                radius = 8f,
                center = Offset(stemX + 10f, stemTop + 5f)
            )
        }
        PlantState.WITHERING -> {
            drawCircle(
                color = Color(0xFF8FBC8F).copy(alpha = 0.6f),
                radius = 8f,
                center = Offset(stemX, stemTop + 5f)
            )
            drawCircle(
                color = Color(0xFF8FBC8F).copy(alpha = 0.4f),
                radius = 6f,
                center = Offset(stemX - 8f, stemTop + 10f)
            )
        }
    }
}

private fun DrawScope.drawCandle(width: Float, height: Float) {
    val candleX = width * 0.8f
    val candleY = height * 0.6f
    val candleWidth = width * 0.02f
    val candleHeight = height * 0.06f
    
    drawRect(
        color = Color(0xFFFFF8DC),
        topLeft = Offset(candleX, candleY),
        size = Size(candleWidth, candleHeight)
    )
    
    drawCircle(
        color = Color(0xFFFFA500),
        radius = 6f,
        center = Offset(candleX + candleWidth / 2, candleY - 3f)
    )
}
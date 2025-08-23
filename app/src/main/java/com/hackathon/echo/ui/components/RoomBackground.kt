package com.hackathon.echo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.hackathon.echo.data.PetState
import com.hackathon.echo.data.EmotionType
import com.hackathon.echo.R

@Composable
fun RoomBackground(
    petState: PetState,
    modifier: Modifier = Modifier
) {
    val backgroundResource = when (petState.emotion) {
        EmotionType.JOY -> R.drawable.h_b
        EmotionType.SADNESS -> R.drawable.s_b
        EmotionType.THOUGHTFUL -> R.drawable.m_b
        EmotionType.CALM -> R.drawable.m_b
        else -> R.drawable.n_b
    }
    
    Image(
        painter = painterResource(id = backgroundResource),
        contentDescription = "Room background for ${petState.emotion.name} state",
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
}


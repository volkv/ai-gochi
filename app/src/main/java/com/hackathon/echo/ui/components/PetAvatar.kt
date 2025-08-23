package com.hackathon.echo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hackathon.echo.data.EmotionType
import com.hackathon.echo.data.PetState
import com.hackathon.echo.R

@Composable
fun PetAvatar(
    petState: PetState,
    size: Dp = 120.dp,
    modifier: Modifier = Modifier
) {
    val imageResource = when (petState.emotion) {
        EmotionType.JOY -> R.drawable.h
        EmotionType.SADNESS -> R.drawable.s
        EmotionType.THOUGHTFUL -> R.drawable.m
        EmotionType.NEUTRAL -> R.drawable.n
    }
    
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageResource),
            contentDescription = "Pet avatar in ${petState.emotion.name} state",
            modifier = Modifier.size(size),
            contentScale = ContentScale.Fit
        )
    }
}


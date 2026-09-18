package com.huming.asharecsvanalyzer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    onPrimaryContainer = PrimaryBlueDark,
    secondary = Color(0xFF546E7A),
    background = SurfaceLight,
    surface = Color.White,
    error = UpRed,
)

private val DarkColors = darkColorScheme(
    primary = PrimaryBlueLight,
    onPrimary = Color.Black,
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = Color(0xFFD6E4FF),
    secondary = Color(0xFF90A4AE),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = UpRed,
)

@Composable
fun AShareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}

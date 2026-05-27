package com.example.skycastproject.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SkyDarkPrimary, background = SkyDarkBackground, surface = SkyDarkSurface,
    onBackground = SkyDarkOnSurface, onSurface = SkyDarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = SkyLightPrimary, background = SkyLightBackground, surface = SkyLightSurface,
    onBackground = SkyLightOnSurface, onSurface = SkyLightOnSurface
)

@Composable
fun SkycastProjectTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

val MaterialTheme.glassBackground: Color
    @Composable get() = if (isSystemInDarkTheme()) GlassBackgroundDark else GlassBackgroundLight

val MaterialTheme.glassBorder: Color
    @Composable get() = if (isSystemInDarkTheme()) GlassBorderDark else GlassBorderLight
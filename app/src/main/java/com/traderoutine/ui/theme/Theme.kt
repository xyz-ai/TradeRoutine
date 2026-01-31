package com.traderoutine.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF111827),
    secondary = Color(0xFF4B5563),
    surface = Color(0xFFFFFFFF),
    background = Color(0xFFF9FAFB)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFF9FAFB),
    secondary = Color(0xFF9CA3AF),
    surface = Color(0xFF111827),
    background = Color(0xFF0F172A)
)

@Composable
fun TradeRoutineTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = TradeRoutineTypography,
        content = content
    )
}

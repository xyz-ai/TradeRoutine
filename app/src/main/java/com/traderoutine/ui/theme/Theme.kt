package com.traderoutine.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = CalmPrimary,
    onPrimary = CalmSurface,
    primaryContainer = CalmPrimaryContainer,
    onPrimaryContainer = CalmText,
    secondary = CalmAccent,
    onSecondary = CalmText,
    secondaryContainer = CalmAccentContainer,
    onSecondaryContainer = CalmText,
    tertiary = CalmSuccess,
    onTertiary = CalmSurface,
    tertiaryContainer = CalmSuccessContainer,
    onTertiaryContainer = CalmText,
    background = CalmBackground,
    onBackground = CalmText,
    surface = CalmSurface,
    onSurface = CalmText,
    surfaceVariant = CalmSurfaceVariant,
    onSurfaceVariant = CalmMutedText,
    outline = CalmDivider
)

private val DarkColors = darkColorScheme(
    primary = CalmPrimaryContainer,
    onPrimary = CalmText,
    primaryContainer = CalmPrimary,
    onPrimaryContainer = CalmSurface,
    secondary = CalmAccentContainer,
    onSecondary = CalmText,
    secondaryContainer = CalmAccent,
    onSecondaryContainer = CalmText,
    tertiary = CalmSuccessContainer,
    onTertiary = CalmText,
    tertiaryContainer = CalmSuccess,
    onTertiaryContainer = CalmSurface,
    background = CalmText,
    onBackground = CalmBackground,
    surface = CalmText,
    onSurface = CalmBackground,
    surfaceVariant = ColorTokens.DarkSurfaceVariant,
    onSurfaceVariant = ColorTokens.DarkSurfaceText,
    outline = ColorTokens.DarkOutline
)

@Composable
fun TradeRoutineTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = TradeRoutineTypography,
        content = content
    )
}

private object ColorTokens {
    val DarkSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF454B42)
    val DarkSurfaceText = androidx.compose.ui.graphics.Color(0xFFD7DDD2)
    val DarkOutline = androidx.compose.ui.graphics.Color(0xFF667062)
}

package com.popchat.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// PopChat Brand Colors - Electric Blue Palette
val PopChatBlue = Color(0xFF1E88E5)
val PopChatBlueLight = Color(0xFF64B5F6)
val PopChatBlueDark = Color(0xFF1565C0)
val PopChatBlueContainer = Color(0xFFE3F2FD)
val PopChatBlueContainerDark = Color(0xFF0D47A1)

// Accent colors from logo
val PopChatGreen = Color(0xFF4CAF50)
val PopChatYellow = Color(0xFFFFC107)
val PopChatOrange = Color(0xFFFF9800)

// Neutral colors
val White = Color.White
val OffWhite = Color(0xFFFAFAFA)
val LightGray = Color(0xFFF5F5F5)
val MediumGray = Color(0xFFE0E0E0)
val DarkGray = Color(0xFF757575)
val Charcoal = Color(0xFF212121)

private val LightColorScheme = lightColorScheme(
    primary = PopChatBlue,
    onPrimary = White,
    primaryContainer = PopChatBlueContainer,
    onPrimaryContainer = PopChatBlueDark,
    secondary = PopChatBlueLight,
    onSecondary = White,
    secondaryContainer = PopChatBlueContainer,
    onSecondaryContainer = PopChatBlueDark,
    tertiary = PopChatGreen,
    onTertiary = White,
    tertiaryContainer = Color(0xFFE8F5E9),
    onTertiaryContainer = Color(0xFF1B5E20),
    error = Color(0xFFD32F2F),
    onError = White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C),
    surface = White,
    onSurface = Charcoal,
    surfaceContainer = OffWhite,
    surfaceContainerHigh = LightGray,
    surfaceContainerHighest = MediumGray,
    surfaceContainerLow = Color(0xFFF8F9FA),
    surfaceContainerLowest = White,
    surfaceBright = White,
    surfaceDim = Color(0xFFF0F0F0),
    outline = MediumGray,
    outlineVariant = Color(0xFFE8E8E8),
    scrim = Color.Black,
    shadow = Color.Black,
    inverseSurface = Charcoal,
    inverseOnSurface = OffWhite,
    inversePrimary = PopChatBlueLight,
    surfaceTint = PopChatBlue
)

private val DarkColorScheme = darkColorScheme(
    primary = PopChatBlueLight,
    onPrimary = Color(0xFF0D47A1),
    primaryContainer = PopChatBlueDark,
    onPrimaryContainer = PopChatBlueContainer,
    secondary = PopChatBlue,
    onSecondary = White,
    secondaryContainer = PopChatBlueDark,
    onSecondaryContainer = PopChatBlueContainer,
    tertiary = Color(0xFF81C784),
    onTertiary = Color(0xFF1B5E20),
    tertiaryContainer = Color(0xFF2E7D32),
    onTertiaryContainer = Color(0xFFE8F5E9),
    error = Color(0xFFEF5350),
    onError = Color(0xFFB71C1C),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFFEBEE),
    surface = Color(0xFF121212),
    onSurface = OffWhite,
    surfaceContainer = Color(0xFF1E1E1E),
    surfaceContainerHigh = Color(0xFF2C2C2C),
    surfaceContainerHighest = Color(0xFF3A3A3A),
    surfaceContainerLow = Color(0xFF1A1A1A),
    surfaceContainerLowest = Color(0xFF0D0D0D),
    surfaceBright = Color(0xFF3A3A3A),
    surfaceDim = Color(0xFF121212),
    outline = Color(0xFF666666),
    outlineVariant = Color(0xFF444444),
    scrim = Color.Black,
    shadow = Color.Black,
    inverseSurface = OffWhite,
    inverseOnSurface = Charcoal,
    inversePrimary = PopChatBlue,
    surfaceTint = PopChatBlueLight
)

@Composable
fun PopChatTheme(
    darkTheme: Boolean = androidx.compose.material3.isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
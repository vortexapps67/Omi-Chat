package com.popchat.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// OmiChat Brand Colors - Electric Blue Palette
val OmiChatBlue = Color(0xFF1E88E5)
val OmiChatBlueLight = Color(0xFF64B5F6)
val OmiChatBlueDark = Color(0xFF1565C0)
val OmiChatBlueContainer = Color(0xFFE3F2FD)
val OmiChatBlueContainerDark = Color(0xFF0D47A1)

// Accent colors from logo
val OmiChatGreen = Color(0xFF4CAF50)
val OmiChatYellow = Color(0xFFFFC107)
val OmiChatOrange = Color(0xFFFF9800)

// Neutral colors
val White = Color.White
val OffWhite = Color(0xFFFAFAFA)
val LightGray = Color(0xFFF5F5F5)
val MediumGray = Color(0xFFE0E0E0)
val DarkGray = Color(0xFF757575)
val Charcoal = Color(0xFF212121)

/**
 * Whether the frosted-glass treatment is switched on app-wide.
 *
 * Screens read this so a single Settings toggle can turn the whole effect off
 * without threading a parameter through every composable.
 */
val LocalGlassEnabled = staticCompositionLocalOf { true }

private val LightColorScheme = lightColorScheme(
    primary = OmiChatBlue,
    onPrimary = White,
    primaryContainer = OmiChatBlueContainer,
    onPrimaryContainer = OmiChatBlueDark,
    secondary = OmiChatBlueLight,
    onSecondary = White,
    secondaryContainer = OmiChatBlueContainer,
    onSecondaryContainer = OmiChatBlueDark,
    tertiary = OmiChatGreen,
    onTertiary = White,
    tertiaryContainer = Color(0xFFE8F5E9),
    onTertiaryContainer = Color(0xFF1B5E20),
    error = Color(0xFFD32F2F),
    onError = White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C),
    surface = Color(0xFFF7FAFE),
    onSurface = Charcoal,
    surfaceContainer = Color(0xFFEFF4FA),
    surfaceContainerHigh = Color(0xFFE7EEF6),
    surfaceContainerHighest = Color(0xFFDEE7F1),
    surfaceContainerLow = Color(0xFFFAFCFF),
    surfaceContainerLowest = White,
    surfaceBright = White,
    surfaceDim = Color(0xFFE8EDF3),
    outline = Color(0xFFCBD6E2),
    outlineVariant = Color(0xFFE3EAF2),
    scrim = Color.Black,
    shadow = Color.Black,
    inverseSurface = Charcoal,
    inverseOnSurface = OffWhite,
    inversePrimary = OmiChatBlueLight,
    surfaceTint = OmiChatBlue
)

private val DarkColorScheme = darkColorScheme(
    primary = OmiChatBlueLight,
    onPrimary = Color(0xFF0D47A1),
    primaryContainer = OmiChatBlueDark,
    onPrimaryContainer = OmiChatBlueContainer,
    secondary = OmiChatBlue,
    onSecondary = White,
    secondaryContainer = OmiChatBlueDark,
    onSecondaryContainer = OmiChatBlueContainer,
    tertiary = Color(0xFF81C784),
    onTertiary = Color(0xFF1B5E20),
    tertiaryContainer = Color(0xFF2E7D32),
    onTertiaryContainer = Color(0xFFE8F5E9),
    error = Color(0xFFEF5350),
    onError = Color(0xFFB71C1C),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFFEBEE),
    surface = Color(0xFF0F141B),
    onSurface = OffWhite,
    surfaceContainer = Color(0xFF1B2430),
    surfaceContainerHigh = Color(0xFF26313F),
    surfaceContainerHighest = Color(0xFF313D4D),
    surfaceContainerLow = Color(0xFF161D27),
    surfaceContainerLowest = Color(0xFF0A0E14),
    surfaceBright = Color(0xFF313D4D),
    surfaceDim = Color(0xFF0F141B),
    outline = Color(0xFF5A6675),
    outlineVariant = Color(0xFF3A4453),
    scrim = Color.Black,
    shadow = Color.Black,
    inverseSurface = OffWhite,
    inverseOnSurface = Charcoal,
    inversePrimary = OmiChatBlue,
    surfaceTint = OmiChatBlueLight
)

/**
 * App theme. Provides both the Material colour scheme and the glass tokens that
 * `glassPanel()` / `GlassBackdrop()` read from.
 *
 * @param glass enables the frosted-glass treatment across the app.
 * @param animatedBackdrop slowly drifts the mesh gradient behind glass panels.
 */
@Composable
fun OmiChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    glass: Boolean = true,
    animatedBackdrop: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val glassTokens = if (darkTheme) DarkGlassTokens else LightGlassTokens

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
    ) {
        CompositionLocalProvider(
            LocalGlassEnabled provides glass,
        ) {
            OmiChatGlass.LocalTokens(glassTokens) {
                if (glass) {
                    GlassScaffold(animated = animatedBackdrop) { content() }
                } else {
                    content()
                }
            }
        }
    }
}
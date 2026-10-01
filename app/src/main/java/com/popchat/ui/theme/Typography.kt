package com.popchat.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// SF Pro style typography - modern, clean, readable
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = androidx.compose.ui.unit.sp(64),
        letterSpacing = androidx.compose.ui.unit.sp(-0.25)
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = androidx.compose.ui.unit.sp(52),
        letterSpacing = androidx.compose.ui.unit.sp(0)
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = androidx.compose.ui.unit.sp(44),
        letterSpacing = androidx.compose.ui.unit.sp(0)
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = androidx.compose.ui.unit.sp(40),
        letterSpacing = androidx.compose.ui.unit.sp(0)
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = androidx.compose.ui.unit.sp(36),
        letterSpacing = androidx.compose.ui.unit.sp(0)
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = androidx.compose.ui.unit.sp(32),
        letterSpacing = androidx.compose.ui.unit.sp(0)
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W600,
        fontSize = 22.sp,
        lineHeight = androidx.compose.ui.unit.sp(28),
        letterSpacing = androidx.compose.ui.unit.sp(0)
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = androidx.compose.ui.unit.sp(24),
        letterSpacing = androidx.compose.ui.unit.sp(0.15)
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = androidx.compose.ui.unit.sp(20),
        letterSpacing = androidx.compose.ui.unit.sp(0.1)
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = androidx.compose.ui.unit.sp(24),
        letterSpacing = androidx.compose.ui.unit.sp(0.5)
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = androidx.compose.ui.unit.sp(20),
        letterSpacing = androidx.compose.ui.unit.sp(0.25)
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = androidx.compose.ui.unit.sp(16),
        letterSpacing = androidx.compose.ui.unit.sp(0.4)
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = androidx.compose.ui.unit.sp(20),
        letterSpacing = androidx.compose.ui.unit.sp(0.1)
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = androidx.compose.ui.unit.sp(16),
        letterSpacing = androidx.compose.ui.unit.sp(0.5)
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = androidx.compose.ui.unit.sp(16),
        letterSpacing = androidx.compose.ui.unit.sp(0.5)
    )
)
package com.popchat.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Corner radii for the app.
 *
 * Named `OmiChatShapes` rather than `Shapes` so it cannot be confused with the
 * Material 3 `Shapes` type it constructs.
 */
val OmiChatShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),   // Small chips, badges
    small = RoundedCornerShape(12.dp),       // Input fields, cards
    medium = RoundedCornerShape(16.dp),      // Message bubbles, larger cards
    large = RoundedCornerShape(24.dp),       // Bottom sheets, modals
    extraLarge = RoundedCornerShape(32.dp),  // Full screen containers
)
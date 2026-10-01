package com.popchat.ui.theme

import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.RoundedCornerShape
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),   // Small chips, badges
    small = RoundedCornerShape(12.dp),       // Input fields, cards
    medium = RoundedCornerShape(16.dp),      // Message bubbles, larger cards
    large = RoundedCornerShape(24.dp),       // Bottom sheets, modals
    extraLarge = RoundedCornerShape(32.dp),  // Full screen containers
    
    // Custom shapes for specific components
    // Pill shape for buttons and filter chips
    // Circle shape for avatars (handled separately via CircleShape)
)
package com.popchat.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.popchat.ui.theme.GlassLevel
import com.popchat.ui.theme.OmiChatBlue
import com.popchat.ui.theme.OmiChatGreen
import com.popchat.ui.theme.glassPanel

/**
 * Circular avatar with initials fallback.
 *
 * @param showOnlineIndicator draws the presence dot. It is drawn only when
 *   [isOnline] is also true, so callers can leave it on for every row and let
 *   the presence data decide.
 */
@Composable
fun Avatar(
    imageUrl: String? = null,
    name: String? = null,
    modifier: Modifier = Modifier,
    size: Int = 48,
    showOnlineIndicator: Boolean = false,
    isOnline: Boolean = false
) {
    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrBlank()) {
            // Subcompose rather than plain AsyncImage so the initials disc stays
            // on screen while the image loads and if it fails - a blank circle
            // for the length of a slow request looks like a broken row.
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = "Avatar for ${name ?: "user"}",
                modifier = Modifier
                    .size(size.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = { InitialsDisc(name = name, size = size) },
                error = { InitialsDisc(name = name, size = size) },
            )
        } else {
            InitialsDisc(name = name, size = size)
        }

        if (showOnlineIndicator && isOnline) {
            Box(
                modifier = Modifier
                    .size((size * 0.3).dp)
                    .align(Alignment.BottomEnd)
                    .background(OmiChatGreen, CircleShape)
                    .border(3.dp, Color.White, CircleShape)
            )
        }
    }
}

/**
 * Initials on a frosted brand disc, shown when there is no avatar image.
 *
 * Avatar placeholders are glass rather than flat brand circles so
 * un-provisioned accounts sit in the same visual language as the panes layered
 * over them.
 */
@Composable
private fun InitialsDisc(name: String?, size: Int) {
    val initials = name
        ?.split(" ")
        ?.mapNotNull { it.firstOrNull()?.uppercase() }
        ?.joinToString("")
        ?.take(2)
        ?: "?"

    Box(
        modifier = Modifier
            .size(size.dp)
            .glassPanel(shape = CircleShape, level = GlassLevel.Thick, accent = OmiChatBlue),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = (size * 0.4f).sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

/** [Avatar] that derives its presence dot from a textual status. */
@Composable
fun StatusAvatar(
    imageUrl: String? = null,
    name: String? = null,
    modifier: Modifier = Modifier,
    size: Int = 48,
    status: String = "online"
) {
    Avatar(
        imageUrl = imageUrl,
        name = name,
        modifier = modifier,
        size = size,
        showOnlineIndicator = true,
        isOnline = status == "online"
    )
}
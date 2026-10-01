package com.popchat.ui.common

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.PlayCircleFill
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.popchat.ui.theme.GlassLevel
import com.popchat.ui.theme.OmiChatBlue
import com.popchat.ui.theme.glassPanel
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/** Message kinds the bubble knows how to render. Mirrors `MessageEntity`'s constants. */
object MessageType {
    const val TEXT = "text"
    const val IMAGE = "image"
    const val VIDEO = "video"
    const val AUDIO = "audio"
    const val FILE = "file"
    const val LOCATION = "location"
    const val CONTACT = "contact"
    const val SYSTEM = "system"
}

/**
 * A single message row.
 *
 * Takes primitives rather than a Room entity so the UI layer stays free of data
 * classes — callers map whatever they hold (entity, network DTO, preview data)
 * onto these parameters.
 *
 * Both bubble variants are frosted: outgoing takes a brand-blue accent, incoming
 * stays neutral. That keeps the sent/received distinction while letting the mesh
 * backdrop show through both, which is what stops the thread reading as a stack
 * of opaque stickers.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    content: String,
    type: String,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier,
    mediaUrl: String? = null,
    isRead: Boolean = false,
    timestamp: Instant? = null,
    showAvatar: Boolean = true,
    showName: Boolean = false,
    senderName: String = "",
    onLongClick: (() -> Unit)? = null,
    onReplyClick: (() -> Unit)? = null,
) {
    val colors = MaterialTheme.colorScheme
    val time = remember(timestamp) { formatMessageTime(timestamp) }

    // Asymmetric corners: the "tail" corner is tightened toward the sender.
    val bubbleShape = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = if (isCurrentUser) 20.dp else 6.dp,
        bottomEnd = if (isCurrentUser) 6.dp else 20.dp,
    )
    val accent = if (isCurrentUser) OmiChatBlue else Color.Unspecified

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp)
            .combinedClickable(
                onClick = { onReplyClick?.invoke() },
                onLongClick = { onLongClick?.invoke() },
            ),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom,
    ) {
        if (!isCurrentUser) {
            if (showAvatar) {
                Avatar(
                    name = senderName,
                    size = 32,
                    modifier = Modifier.padding(end = 8.dp, bottom = 2.dp),
                )
            } else {
                Spacer(modifier = Modifier.size(32.dp).padding(end = 8.dp))
            }
        }

        Column(
            horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            if (showName && !isCurrentUser && senderName.isNotBlank()) {
                Text(
                    text = senderName,
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(start = 14.dp),
                )
            }

            Box(
                modifier = Modifier
                    .glassPanel(
                        shape = bubbleShape,
                        level = GlassLevel.Regular,
                        accent = accent,
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                val contentColor = if (isCurrentUser) Color.White else colors.onSurface

                when (type) {
                    MessageType.TEXT -> Text(
                        text = content,
                        color = contentColor,
                        fontSize = 16.sp,
                    )

                    MessageType.IMAGE -> mediaUrl?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "Image",
                            modifier = Modifier
                                .widthIn(min = 150.dp, max = 250.dp)
                                .height(180.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop,
                        )
                    }

                    MessageType.VIDEO -> Box(
                        modifier = Modifier
                            .widthIn(min = 150.dp, max = 250.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(14.dp)),
                    ) {
                        mediaUrl?.let { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = "Video thumbnail",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        }
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircleFill,
                                contentDescription = "Play video",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(48.dp),
                            )
                        }
                    }

                    MessageType.AUDIO -> Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircleFill,
                            contentDescription = "Play voice message",
                            tint = contentColor,
                            modifier = Modifier.size(28.dp),
                        )
                        Text(
                            text = content.ifBlank { "Voice message" },
                            color = contentColor,
                            fontSize = 14.sp,
                        )
                    }

                    else -> Text(
                        text = content.ifBlank { "[$type]" },
                        color = contentColor.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                    )
                }
            }

            Row(
                modifier = Modifier.padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = time,
                    fontSize = 10.sp,
                    color = colors.onSurfaceVariant,
                )
                if (isCurrentUser) {
                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = if (isRead) "Read" else "Delivered",
                        tint = if (isRead) OmiChatBlue else colors.onSurfaceVariant,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }

        if (isCurrentUser && showAvatar) {
            Avatar(
                name = "You",
                size = 32,
                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp),
            )
        }
    }
}

private fun formatMessageTime(instant: Instant?): String {
    if (instant == null) return ""
    val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${local.hour.toString().padStart(2, '0')}:${local.minute.toString().padStart(2, '0')}"
}

/** Glass pill date separator. */
@Composable
fun DateSeparator(dateText: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .glassPanel(
                    shape = RoundedCornerShape(12.dp),
                    level = GlassLevel.Thin,
                )
                .padding(horizontal = 14.dp, vertical = 6.dp),
        ) {
            Text(
                text = dateText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Three-dot typing indicator on a frosted bubble.
 *
 * The dots are driven by one infinite transition with staggered delays rather
 * than three independent ones, so the animation stays on a single shared frame
 * clock instead of scheduling redundant ones.
 */
@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "typing")
    val dotColor = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom,
    ) {
        Avatar(name = "...", size = 32, modifier = Modifier.padding(end = 8.dp))

        Row(
            modifier = Modifier
                .glassPanel(
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = 6.dp,
                        bottomEnd = 20.dp,
                    ),
                    level = GlassLevel.Regular,
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(3) { index ->
                val scale by transition.animateFloat(
                    initialValue = 0.4f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 600, delayMillis = index * 160),
                        repeatMode = RepeatMode.Reverse,
                    ),
                    label = "dot$index",
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .background(dotColor, CircleShape),
                )
            }
        }
    }
}
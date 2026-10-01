package com.popchat.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.data.model.MessageEntity
import com.popchat.ui.theme.PopChatBlue
import com.popchat.ui.theme.PopChatBlueContainer
import com.popchat.ui.theme.PopChatTheme

@Composable
fun MessageBubble(
    message: MessageEntity,
    isCurrentUser: Boolean,
    showAvatar: Boolean = true,
    showName: Boolean = false,
    senderName: String = "",
    onLongClick: (() -> Unit)? = null,
    onReplyClick: (() -> Unit)? = null
) {
    val colors = PopChatTheme.colorScheme
    val time = formatMessageTime(message.createdAt)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .pointerInput(Unit) {
                onLongClick?.let { androidx.compose.foundation.gestures.detectTapGestures(onLongPress = it) }
            },
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        // Other user's avatar
        if (!isCurrentUser && showAvatar) {
            Avatar(
                name = senderName,
                size = 32,
                modifier = Modifier.padding(end = 8.dp, bottom = 4.dp)
            )
        } else if (!isCurrentUser) {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(32.dp).padding(end = 8.dp, bottom = 4.dp))
        }

        Column(
            horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Sender name for group chats
            if (showName && !isCurrentUser && senderName.isNotBlank()) {
                Text(
                    text = senderName,
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            // Message bubble
            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .background(
                        color = if (isCurrentUser) PopChatBlue else colors.surfaceContainerHighest,
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                            bottomEnd = if (isCurrentUser) 4.dp else 16.dp
                        )
                    )
                    .pointerInput(Unit) {
                        onReplyClick?.let { androidx.compose.foundation.gestures.detectTapGestures(onLongPress = it) }
                    }
            ) {
                when (message.type) {
                    MessageEntity.TYPE_TEXT -> {
                        Text(
                            text = message.content,
                            color = if (isCurrentUser) colors.onPrimary else colors.onSurface,
                            fontSize = 16.sp,
                            maxLines = Int.MAX_VALUE
                        )
                    }
                    MessageEntity.TYPE_IMAGE -> {
                        message.mediaUrl?.let { url ->
                            io.coil.compose.AsyncImage(
                                model = url,
                                contentDescription = "Image",
                                modifier = Modifier
                                    .widthIn(min = 150.dp, max = 250.dp)
                                    .aspectRatio(1.5f)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }
                    }
                    MessageEntity.TYPE_VIDEO -> {
                        // Video thumbnail with play button
                        Box(
                            modifier = Modifier
                                .widthIn(min = 150.dp, max = 250.dp)
                                .aspectRatio(1.5f)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            message.mediaUrl?.let { url ->
                                io.coil.compose.AsyncImage(
                                    model = url,
                                    contentDescription = "Video thumbnail",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            }
                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.PlayCircleFill,
                                    contentDescription = "Play video",
                                    tint = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                    }
                    else -> {
                        Text(
                            text = "[${message.type}]",
                            color = if (isCurrentUser) colors.onPrimary else colors.onSurface,
                            fontSize = 14.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }

            // Time and read receipt
            Row(
                modifier = Modifier.padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = time,
                    fontSize = 10.sp,
                    color = if (isCurrentUser) colors.onPrimary.copy(alpha = 0.7f) else colors.onSurfaceVariant
                )
                if (isCurrentUser) {
                    // Read receipt - double check
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.DoneAll,
                        contentDescription = "Read",
                        tint = if (message.readAt != null) PopChatBlue else colors.onPrimary.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Current user avatar
        if (isCurrentUser && showAvatar) {
            Avatar(
                name = "You",
                size = 32,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
        }
    }
}

private fun formatMessageTime(instant: kotlinx.datetime.Instant): String {
    val formatter = kotlinx.datetime.format.DateTimeFormatter.ofPattern("HH:mm")
    return formatter.format(instant)
}

// Date separator in chat
@Composable
fun DateSeparator(dateText: String) {
    val colors = PopChatTheme.colorScheme
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(colors.surfaceContainerHighest, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = dateText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colors.onSurfaceVariant
            )
        }
    }
}

// Typing indicator
@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    val colors = PopChatTheme.colorScheme
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Avatar(name = "...", size = 32, modifier = Modifier.padding(end = 8.dp))
        Box(
            modifier = Modifier
                .background(colors.surfaceContainerHighest, RoundedCornerShape(16.dp).copy(bottomStart = 4.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.animation.core.InfiniteTransition
                    .runInfinite(
                        androidx.compose.animation.core.rememberInfiniteTransition(label = "")
                    )
                    .let { transition ->
                        (0..2).map { index ->
                            val animatedFloat = transition.animateFloat(
                                initialValue = 0f,
                                targetValue = 1f,
                                animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                                    animation = androidx.compose.animation.core.tween(
                                        durationMillis = 600,
                                        delayMillis = index * 150,
                                        easing = androidx.compose.animation.core.Easing.InOutCubic
                                    ),
                                    repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                                ),
                                label = "dot$index"
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .graphicsLayer { scaleY = animatedFloat.value; scaleX = animatedFloat.value }
                                    .background(colors.onSurfaceVariant, androidx.compose.ui.graphics.CircleShape)
                            )
                        }
                    }
            }
        }
    }
}
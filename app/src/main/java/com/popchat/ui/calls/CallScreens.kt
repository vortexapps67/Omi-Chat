package com.popchat.ui.calls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.ui.common.Avatar
import com.popchat.ui.theme.PopChatBlue
import com.popchat.ui.theme.PopChatGreen
import com.popchat.ui.theme.PopChatTheme

@Composable
fun VoiceCallScreen(
    contactName: String,
    contactAvatar: String? = null,
    isIncoming: Boolean = true,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onMute: () -> Unit,
    onSpeaker: () -> Unit,
    onVideo: () -> Unit,
    onEnd: () -> Unit
) {
    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Gradient background
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            androidx.compose.foundation.background.Background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0D47A1), // Dark blue
                        Color(0xFF1565C0),
                        Color(0xFF1E88E5), // PopChatBlue
                        Color(0xFF006064)  // Teal
                    ),
                    center = androidx.compose.ui.geometry.Offset(0.5f, 0.5f),
                    radius = 1.2f
                )
            )
        }

        // Glowing rings animation
        val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "")
        val pulseAlpha = infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 0.1f,
            animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                animation = androidx.compose.animation.core.tween(
                    durationMillis = 2000,
                    easing = androidx.compose.animation.core.LinearEasing
                ),
                repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
            ),
            label = "pulse"
        )

        Box(
            modifier = Modifier
                .size(300.dp)
                .background(Color.Transparent, CircleShape)
                .graphicsLayer { alpha = pulseAlpha.value }
        ) {
            // Could add animated rings here
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Call type indicator
            if (isIncoming) {
                Badge(
                    badgeContent = {
                        Text(
                            text = "Voice Call",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    },
                    backgroundColor = PopChatBlue.copy(alpha = 0.9f)
                ) {
                    androidx.compose.foundation.layout.Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }
            }

            // Avatar
            Avatar(
                imageUrl = contactAvatar,
                name = contactName,
                size = 160,
                showOnlineIndicator = false
            )

            // Name and status
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = contactName,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.ui.graphics.Color.White
                )

                Text(
                    text = if (isIncoming) "Incoming..." else "Connecting...",
                    fontSize = 16.sp,
                    color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.8f)
                )
            }

            // Call duration (for active calls)
            if (!isIncoming) {
                Text(
                    text = "02:34",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Medium,
                    color = androidx.compose.ui.graphics.Color.White
                )
            }

            // Call Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Mute
                CallControlButton(
                    icon = if (isMuted) androidx.compose.material.icons.Icons.Default.MicOff else androidx.compose.material.icons.Icons.Default.Mic,
                    label = "Mute",
                    isActive = isMuted,
                    onClick = {
                        isMuted = !isMuted
                        onMute()
                    }
                )

                // Speaker
                CallControlButton(
                    icon = if (isSpeakerOn) androidx.compose.material.icons.Icons.Default.VolumeUp else androidx.compose.material.icons.Icons.Default.VolumeOff,
                    label = "Speaker",
                    isActive = isSpeakerOn,
                    onClick = {
                        isSpeakerOn = !isSpeakerOn
                        onSpeaker()
                    }
                )

                // Video
                CallControlButton(
                    icon = androidx.compose.material.icons.Icons.Default.Videocam,
                    label = "Video",
                    onClick = onVideo
                )
            }

            // End Call - prominent red button
            if (isIncoming) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(40.dp)
                ) {
                    // Decline
                    CallControlButton(
                        icon = androidx.compose.material.icons.Icons.Default.CallEnd,
                        label = "Decline",
                        isDestructive = true,
                        size = 64,
                        onClick = onDecline
                    )

                    // Accept
                    CallControlButton(
                        icon = androidx.compose.material.icons.Icons.Default.Call,
                        label = "Accept",
                        isPrimary = true,
                        size = 64,
                        onClick = onAccept
                    )
                }
            } else {
                CallControlButton(
                    icon = androidx.compose.material.icons.Icons.Default.CallEnd,
                    label = "End Call",
                    isDestructive = true,
                    size = 72,
                    onClick = onEnd
                )
            }
        }
    }
}

@Composable
fun VideoCallScreen(
    contactName: String,
    contactAvatar: String? = null,
    isIncoming: Boolean = true,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onMute: () -> Unit,
    onFlipCamera: () -> Unit,
    onEnd: () -> Unit
) {
    var isMuted by remember { mutableStateOf(false) }
    var isFrontCamera by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Full screen video feed (remote)
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Placeholder for remote video
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Gradient background as placeholder
                androidx.compose.foundation.background.Background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Black, Color(0xFF1A1A2E), Color.Black)
                    )
                )
                
                // Contact name overlay
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = contactName,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                    Text(
                        text = if (isIncoming) "Incoming Video Call..." else "Connecting...",
                        fontSize = 16.sp,
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Picture-in-Picture local video
            Box(
                modifier = Modifier
                    .size(120.dp, 160.dp)
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF333333), RoundedCornerShape(16.dp))
                        .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                ) {
                    Text(
                        text = "You",
                        fontSize = 14.sp,
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.5f)
                    )
                }
            }

            // Call duration
            if (!isIncoming) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 64.dp)
                ) {
                    Text(
                        text = "02:34",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
        }

        // Bottom controls
        if (isIncoming) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 32.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                CallControlButton(
                    icon = androidx.compose.material.icons.Icons.Default.CallEnd,
                    label = "Decline",
                    isDestructive = true,
                    size = 64,
                    onClick = onDecline
                )

                CallControlButton(
                    icon = androidx.compose.material.icons.Icons.Default.Videocam,
                    label = "Accept",
                    isPrimary = true,
                    size = 64,
                    onClick = onAccept
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 32.dp)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                CallControlButton(
                    icon = if (isMuted) androidx.compose.material.icons.Icons.Default.MicOff else androidx.compose.material.icons.Icons.Default.Mic,
                    label = "Mute",
                    isActive = isMuted,
                    onClick = {
                        isMuted = !isMuted
                        onMute()
                    }
                )

                CallControlButton(
                    icon = androidx.compose.material.icons.Icons.Default.SwitchCamera,
                    label = "Flip",
                    onClick = {
                        isFrontCamera = !isFrontCamera
                        onFlipCamera()
                    }
                )

                CallControlButton(
                    icon = androidx.compose.material.icons.Icons.Default.CallEnd,
                    label = "End",
                    isDestructive = true,
                    size = 64,
                    onClick = onEnd
                )
            }
        }
    }
}

@Composable
fun CallControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean = false,
    isPrimary: Boolean = false,
    isDestructive: Boolean = false,
    size: Int = 56,
    onClick: () -> Unit
) {
    val colors = PopChatTheme.colorScheme
    
    val (backgroundColor, iconColor) = when {
        isDestructive -> androidx.compose.ui.graphics.Color(0xFFEF4444) to androidx.compose.ui.graphics.Color.White
        isPrimary -> PopChatBlue to androidx.compose.ui.graphics.Color.White
        isActive -> PopChatBlue.copy(alpha = 0.2f) to PopChatBlue
        else -> colors.surfaceContainerHighest.copy(alpha = 0.3f) to androidx.compose.ui.graphics.Color.White
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .background(backgroundColor, CircleShape)
                .pointerInput(Unit) {
                    androidx.compose.foundation.gestures.detectTapGestures(onTap = onClick)
                }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(28.dp).padding(8.dp)
            )
        }

        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = androidx.compose.ui.graphics.Color.White
        )
    }
}

@Composable
fun Badge(
    badgeContent: @Composable () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        badgeContent()
    }
}

@Composable
fun CallHistoryScreen(
    onBack: () -> Unit,
    onCallBack: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        // Top App Bar
        androidx.compose.material3.TopAppBar(
            title = { Text("Calls") },
            navigationIcon = {
                androidx.compose.material3.IconButton(onClick = onBack) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                containerColor = PopChatTheme.colorScheme.surfaceContainerLow
            )
        )

        // Filter tabs
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf("All", "Missed", "Outgoing", "Incoming")) { filter ->
                    FilterChip(
                        text = filter,
                        isSelected = filter == "All",
                        onClick = { /* Filter */ }
                    )
                }
            }
        }

        // Call history list
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(callHistory) { call ->
                CallHistoryItem(call = call, onCallBack = onCallBack)
                androidx.compose.material3.Divider(
                    modifier = Modifier.padding(start = 72.dp),
                    color = PopChatTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = PopChatTheme.colorScheme
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(
                color = if (isSelected) PopChatBlue else colors.surfaceContainerHighest,
                shape = androidx.compose.ui.graphics.RoundedCornerShape(20.dp)
            )
            .pointerInput(Unit) {
                androidx.compose.foundation.gestures.detectTapGestures(onTap = onClick)
            }
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) androidx.compose.ui.graphics.Color.White else colors.onSurfaceVariant
        )
    }
}

data class CallHistoryItem(
    val name: String,
    val avatarUrl: String?,
    val type: CallType,
    val timestamp: kotlinx.datetime.Instant,
    val duration: String?
)

enum class CallType {
    INCOMING, OUTGOING, MISSED, VIDEO_INCOMING, VIDEO_OUTGOING
}

val callHistory = listOf(
    CallHistoryItem("Riya Sharma", null, CallType.INCOMING, kotlinx.datetime.Instant.now().minusSeconds(3600), "05:23"),
    CallHistoryItem("Akshansh Sinha", null, CallType.OUTGOING, kotlinx.datetime.Instant.now().minusSeconds(7200), "12:45"),
    CallHistoryItem("Sarah Johnson", null, CallType.MISSED, kotlinx.datetime.Instant.now().minusSeconds(86400), null),
    CallHistoryItem("Dev Team 🚀", null, CallType.VIDEO_INCOMING, kotlinx.datetime.Instant.now().minusSeconds(172800), "08:12"),
    CallHistoryItem("Design Squad", null, CallType.VIDEO_OUTGOING, kotlinx.datetime.Instant.now().minusSeconds(259200), "15:30")
)

@Composable
fun CallHistoryItem(
    call: CallHistoryItem,
    onCallBack: (String) -> Unit
) {
    val colors = PopChatTheme.colorScheme
    val time = formatTime(call.timestamp)

    androidx.compose.material3.ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(colors.surface)
            .clickable(onClick = { onCallBack(call.name) }),
        leading = {
            com.popchat.ui.common.Avatar(
                imageUrl = call.avatarUrl,
                name = call.name,
                size = 56
            )
        },
        headlineContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = call.name,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = time,
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (call.type) {
                        CallType.INCOMING -> Icon(androidx.compose.material.icons.Icons.Default.CallReceived, contentDescription = "Incoming", tint = PopChatGreen, modifier = Modifier.size(16.dp))
                        CallType.OUTGOING -> Icon(androidx.compose.material.icons.Icons.Default.CallMade, contentDescription = "Outgoing", tint = PopChatBlue, modifier = Modifier.size(16.dp))
                        CallType.MISSED -> Icon(androidx.compose.material.icons.Icons.Default.CallMissed, contentDescription = "Missed", tint = colors.error, modifier = Modifier.size(16.dp))
                        CallType.VIDEO_INCOMING, CallType.VIDEO_OUTGOING -> {
                            Icon(androidx.compose.material.icons.Icons.Default.Videocam, contentDescription = "Video", tint = PopChatBlue, modifier = Modifier.size(16.dp))
                            Text(call.type.name.replace("_", " "), fontSize = 12.sp, color = colors.onSurfaceVariant)
                        }
                    }
                    call.duration?.let { dur ->
                        Text(text = dur, fontSize = 12.sp, color = colors.onSurfaceVariant)
                    }
                }
            }
        },
        trailing = {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Call,
                contentDescription = "Call back",
                tint = PopChatBlue,
                modifier = Modifier.padding(start = 8.dp).size(24.dp)
            )
        }
    )
}

private fun formatTime(instant: kotlinx.datetime.Instant): String {
    val now = kotlinx.datetime.Instant.now()
    val diff = now.epochMilliseconds - instant.epochMilliseconds
    val minutes = diff / (1000 * 60)
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "now"
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        days < 7 -> "${days}d"
        else -> kotlinx.datetime.format.DateTimeFormatter.ISO_LOCAL_DATE.format(instant)
    }
}
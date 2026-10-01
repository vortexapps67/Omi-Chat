package com.popchat.ui.calls

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.ui.common.Avatar
import com.popchat.ui.theme.GlassLevel
import com.popchat.ui.theme.OmiChatBlue
import com.popchat.ui.theme.OmiChatGreen
import com.popchat.ui.theme.glassPanel

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
        contentAlignment = Alignment.Center,
    ) {
        // Call surfaces are dark by design so the participant stays the focus;
        // the gradient replaces the mesh backdrop here.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0D47A1), // Deep blue
                            Color(0xFF1565C0),
                            Color(0xFF1E88E5), // OmiChatBlue
                            Color(0xFF006064), // Teal
                        ),
                    ),
                ),
        )

        // Pulsing halo behind the avatar, on a single shared frame clock.
        val pulse = rememberInfiniteTransition(label = "callPulse").animateFloat(
            initialValue = 0.94f,
            targetValue = 1.12f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "pulse",
        )

        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(pulse.value)
                .graphicsLayer { alpha = 0.30f }
                .background(
                    color = Color.White.copy(alpha = 0.18f),
                    shape = CircleShape,
                ),
        )

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
                            color = Color.White
                        )
                    },
                    backgroundColor = OmiChatBlue.copy(alpha = 0.9f)
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
                    color = Color.White
                )

                Text(
                    text = if (isIncoming) "Incoming..." else "Connecting...",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // Call duration (for active calls)
            if (!isIncoming) {
                Text(
                    text = "02:34",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            // Call Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Mute
                CallControlButton(
                    icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    label = "Mute",
                    isActive = isMuted,
                    onClick = {
                        isMuted = !isMuted
                        onMute()
                    }
                )

                // Speaker
                CallControlButton(
                    icon = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                    label = "Speaker",
                    isActive = isSpeakerOn,
                    onClick = {
                        isSpeakerOn = !isSpeakerOn
                        onSpeaker()
                    }
                )

                // Video
                CallControlButton(
                    icon = Icons.Default.Videocam,
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
                        icon = Icons.Default.CallEnd,
                        label = "Decline",
                        isDestructive = true,
                        size = 64,
                        onClick = onDecline
                    )

                    // Accept
                    CallControlButton(
                        icon = Icons.Default.Call,
                        label = "Accept",
                        isPrimary = true,
                        size = 64,
                        onClick = onAccept
                    )
                }
            } else {
                CallControlButton(
                    icon = Icons.Default.CallEnd,
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
                .background(androidx.compose.ui.graphics.Brush.verticalGradient(
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
                        color = Color.White
                    )
                    Text(
                        text = if (isIncoming) "Incoming Video Call..." else "Connecting...",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.7f)
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
                        color = Color.White.copy(alpha = 0.5f)
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
                        color = Color.White
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
                    icon = Icons.Default.CallEnd,
                    label = "Decline",
                    isDestructive = true,
                    size = 64,
                    onClick = onDecline
                )

                CallControlButton(
                    icon = Icons.Default.Videocam,
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
                    icon = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    label = "Mute",
                    isActive = isMuted,
                    onClick = {
                        isMuted = !isMuted
                        onMute()
                    }
                )

                CallControlButton(
                    icon = Icons.Default.SwitchCamera,
                    label = "Flip",
                    onClick = {
                        isFrontCamera = !isFrontCamera
                        onFlipCamera()
                    }
                )

                CallControlButton(
                    icon = Icons.Default.CallEnd,
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
    val colors = MaterialTheme.colorScheme
    
    val (backgroundColor, iconColor) = when {
        isDestructive -> androidx.compose.ui.graphics.Color(0xFFEF4444) to Color.White
        isPrimary -> OmiChatBlue to Color.White
        isActive -> OmiChatBlue.copy(alpha = 0.2f) to OmiChatBlue
        else -> colors.surfaceContainerHighest.copy(alpha = 0.3f) to Color.White
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
            color = Color.White
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
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
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
                    color = MaterialTheme.colorScheme.outlineVariant,
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
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(
                color = if (isSelected) OmiChatBlue else colors.surfaceContainerHighest,
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
            color = if (isSelected) Color.White else colors.onSurfaceVariant
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
    val colors = MaterialTheme.colorScheme
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
                        CallType.INCOMING -> Icon(Icons.Default.CallReceived, contentDescription = "Incoming", tint = OmiChatGreen, modifier = Modifier.size(16.dp))
                        CallType.OUTGOING -> Icon(Icons.Default.CallMade, contentDescription = "Outgoing", tint = OmiChatBlue, modifier = Modifier.size(16.dp))
                        CallType.MISSED -> Icon(Icons.Default.CallMissed, contentDescription = "Missed", tint = colors.error, modifier = Modifier.size(16.dp))
                        CallType.VIDEO_INCOMING, CallType.VIDEO_OUTGOING -> {
                            Icon(Icons.Default.Videocam, contentDescription = "Video", tint = OmiChatBlue, modifier = Modifier.size(16.dp))
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
                imageVector = Icons.Default.Call,
                contentDescription = "Call back",
                tint = OmiChatBlue,
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
package com.popchat.ui.calls

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallMissed
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.SwitchCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.ui.common.Avatar
import com.popchat.ui.common.CircleIconButton
import com.popchat.ui.theme.GlassLevel
import com.popchat.ui.theme.OmiChatBlue
import com.popchat.ui.theme.OmiChatGreen
import com.popchat.ui.theme.glassPanel
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Colour the call surfaces are built from.
 *
 * Call screens are deliberately dark and saturated rather than glass-on-mesh:
 * the participant has to stay the focus, and a frosted pane over a moving
 * backdrop would compete with the video. Glass is used only for the chrome
 * floating over that backdrop - the control pads, the incoming-call pill and
 * the picture-in-picture tile - where it separates the control from the feed
 * without tinting the feed itself.
 */
private val CallDeepBlue = Color(0xFF0D47A1)
private val CallMidBlue = Color(0xFF1565C0)
private val CallBrandBlue = Color(0xFF1E88E5)
private val CallTeal = Color(0xFF006064)
private val CallDanger = Color(0xFFEF4444)

/** White glass tint, since call chrome sits on dark surfaces, not the app's light theme. */
private val CallGlassTint = Color(0xFFFFFFFF)

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CallDeepBlue,
                            CallMidBlue,
                            CallBrandBlue,
                            CallTeal,
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
            if (isIncoming) {
                GlassPill(tint = CallGlassTint) {
                    Text(
                        text = "Voice Call",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            Avatar(
                imageUrl = contactAvatar,
                name = contactName,
                size = 160,
                showOnlineIndicator = false
            )

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

            if (!isIncoming) {
                Text(
                    text = "02:34",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            Row(
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
                    icon = if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                    label = "Speaker",
                    isActive = isSpeakerOn,
                    onClick = {
                        isSpeakerOn = !isSpeakerOn
                        onSpeaker()
                    }
                )

                CallControlButton(
                    icon = Icons.Default.Videocam,
                    label = "Video",
                    onClick = onVideo
                )
            }

            if (isIncoming) {
                Row(
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black, Color(0xFF1A1A2E), Color.Black)
                    )
                )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
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

            if (!isIncoming) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 64.dp)
                        .glassPanel(
                            shape = RoundedCornerShape(20.dp),
                            level = GlassLevel.Thin,
                            accent = CallGlassTint,
                        )
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "02:34",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            // Picture-in-picture local video. Opaque, because a frosted pane
            // over a placeholder would just look like a grey rectangle.
            Box(
                modifier = Modifier
                    .size(120.dp, 160.dp)
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color(0xFF333333), RoundedCornerShape(16.dp))
                    .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isFrontCamera) "You" else "You (rear)",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }

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

/**
 * A round call control.
 *
 * The mute/speaker/flip pads are frosted so they read as floating over the
 * call. Accept and end-call stay solid: they are the two actions a user has to
 * find instantly under pressure, and a translucent button is the wrong thing
 * to put under a thumb that has to be certain.
 */
@Composable
fun CallControlButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean = false,
    isPrimary: Boolean = false,
    isDestructive: Boolean = false,
    size: Int = 56,
    onClick: () -> Unit
) {
    val isSolid = isPrimary || isDestructive

    val backgroundColor = when {
        isDestructive -> CallDanger
        isPrimary -> CallBrandBlue
        else -> Color.White.copy(alpha = if (isActive) 0.28f else 0.16f)
    }
    val iconColor = when {
        isDestructive || isPrimary -> Color.White
        isActive -> Color.White
        else -> Color.White.copy(alpha = 0.85f)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .then(
                    if (isSolid) {
                        Modifier
                    } else {
                        Modifier.glassPanel(
                            shape = CircleShape,
                            level = GlassLevel.Thin,
                            accent = CallGlassTint,
                        )
                    }
                )
                .background(backgroundColor, CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size((size * 0.5f).dp)
            )
        }

        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

/**
 * Small frosted label. Named for what it is rather than `Badge`, which
 * Material 3 already defines with an incompatible signature.
 */
@Composable
private fun GlassPill(
    tint: Color = CallGlassTint,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .glassPanel(
                shape = RoundedCornerShape(16.dp),
                level = GlassLevel.Thin,
                accent = tint,
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
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
        // Hand-rolled app bar rather than TopAppBar: the title and back button
        // need to sit on a frosted bar, and TopAppBar takes its container
        // colour as a plain colour with no glass treatment.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .glassPanel(
                    shape = RoundedCornerShape(0.dp),
                    level = GlassLevel.Thick,
                    grain = false,
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIconButton(
                icon = Icons.Default.ArrowBack,
                onClick = onBack,
                backgroundColor = OmiChatBlue.copy(alpha = 0.1f),
                iconColor = MaterialTheme.colorScheme.onSurface,
                contentDescription = "Back",
            )

            Text(
                text = "Calls",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            )
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("All", "Missed", "Outgoing", "Incoming")) { filter ->
                CallFilterChip(
                    text = filter,
                    isSelected = filter == "All",
                    onClick = { /* Filtering is wired up once call history is persisted. */ }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(callHistory) { call ->
                CallHistoryRow(call = call, onCallBack = onCallBack)
            }
        }
    }
}

@Composable
private fun CallFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .then(
                if (isSelected) {
                    Modifier
                } else {
                    Modifier.glassPanel(
                        shape = RoundedCornerShape(20.dp),
                        level = GlassLevel.Thin,
                    )
                }
            )
            .background(
                color = if (isSelected) OmiChatBlue else colors.surfaceVariant.copy(alpha = 0.35f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
    val timestamp: Instant,
    val duration: String?
)

enum class CallType {
    INCOMING, OUTGOING, MISSED, VIDEO_INCOMING, VIDEO_OUTGOING
}

/**
 * Placeholder history.
 *
 * Real entries arrive from the `calls` table once it is persisted; until then
 * this is what the screen renders so the layout can be reviewed.
 */
private val callHistory: List<CallHistoryItem> = run {
    val now = Clock.System.now()
    listOf(
        CallHistoryItem("Riya Sharma", null, CallType.INCOMING, now - 1.hours, "05:23"),
        CallHistoryItem("Akshansh Sinha", null, CallType.OUTGOING, now - 2.hours, "12:45"),
        CallHistoryItem("Sarah Johnson", null, CallType.MISSED, now - 86400.seconds, null),
        CallHistoryItem("Dev Team", null, CallType.VIDEO_INCOMING, now - 172800.seconds, "08:12"),
        CallHistoryItem("Design Squad", null, CallType.VIDEO_OUTGOING, now - 259200.seconds, "15:30"),
    )
}

@Composable
private fun CallHistoryRow(
    call: CallHistoryItem,
    onCallBack: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val time = formatTime(call.timestamp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassPanel(
                shape = MaterialTheme.shapes.medium,
                level = GlassLevel.Regular,
            )
            .clickable { onCallBack(call.name) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Avatar(
            imageUrl = call.avatarUrl,
            name = call.name,
            size = 56
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = call.name,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurface,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CallTypeIcon(call.type)
                Text(
                    text = call.type.displayLabel,
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant
                )
                call.duration?.let { duration ->
                    Text(
                        text = duration,
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }
        }

        Icon(
            imageVector = Icons.Default.Call,
            contentDescription = "Call back",
            tint = OmiChatBlue,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun CallTypeIcon(type: CallType) {
    val colors = MaterialTheme.colorScheme
    val (icon, tint, description) = when (type) {
        CallType.INCOMING -> Triple(Icons.Default.CallReceived, OmiChatGreen, "Incoming")
        CallType.OUTGOING -> Triple(Icons.Default.CallMade, OmiChatBlue, "Outgoing")
        CallType.MISSED -> Triple(Icons.Default.CallMissed, colors.error, "Missed")
        CallType.VIDEO_INCOMING,
        CallType.VIDEO_OUTGOING -> Triple(Icons.Default.Videocam, OmiChatBlue, "Video")
    }

    Icon(
        imageVector = icon,
        contentDescription = description,
        tint = tint,
        modifier = Modifier.size(16.dp)
    )
}

private val CallType.displayLabel: String
    get() = when (this) {
        CallType.INCOMING -> "Incoming"
        CallType.OUTGOING -> "Outgoing"
        CallType.MISSED -> "Missed"
        CallType.VIDEO_INCOMING -> "Incoming video"
        CallType.VIDEO_OUTGOING -> "Outgoing video"
    }

/** Compact relative time, falling back to a calendar date past a week. */
private fun formatTime(instant: Instant): String {
    val elapsed = Clock.System.now() - instant

    val minutes = elapsed.inWholeMinutes
    val hours = elapsed.inWholeHours
    val days = elapsed.inWholeDays

    return when {
        minutes < 1 -> "now"
        minutes < 60 -> "${minutes}m"
        hours < 24 -> "${hours}h"
        days < 7 -> "${days}d"
        else -> instant
            // LocalDate.toString() is already ISO-8601 (yyyy-MM-dd), which is
            // what this wants; reaching for a DateTimeFormat object here would
            // mean formatting a LocalDateTime just to drop the time part.
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
            .toString()
    }
}
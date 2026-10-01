package com.popchat.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.ui.common.Avatar
import com.popchat.ui.common.MessageBubble
import com.popchat.ui.common.MessageInputBar
import com.popchat.ui.common.MessageType
import com.popchat.ui.theme.GlassLevel
import com.popchat.ui.theme.OmiChatGreen
import com.popchat.ui.theme.glassPanel
import kotlinx.datetime.Instant

@Composable
fun ChatScreen(
    chatName: String,
    chatAvatar: String? = null,
    isGroup: Boolean = false,
    isOnline: Boolean = false,
    onBack: () -> Unit,
    onCall: () -> Unit,
    onVideoCall: () -> Unit,
    onMoreOptions: () -> Unit,
    messages: List<ChatMessage> = sampleMessages,
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Keep the newest message in view as the thread grows.
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            modifier = Modifier.glassPanel(
                shape = RoundedCornerShape(0.dp),
                level = GlassLevel.Thick,
            ),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Avatar(
                        imageUrl = chatAvatar,
                        name = chatName,
                        size = 40,
                        showOnlineIndicator = !isGroup,
                        isOnline = isOnline,
                    )
                    Column {
                        Text(
                            text = chatName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = if (isGroup) "Group \u2022 5 members" else if (isOnline) "Online" else "Offline",
                            fontSize = 12.sp,
                            color = if (isOnline && !isGroup) {
                                OmiChatGreen
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            },
            actions = {
                IconButton(onClick = onCall) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Voice call",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                IconButton(onClick = onVideoCall) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video call",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                IconButton(onClick = onMoreOptions) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = listState,
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            items(messages, key = { it.id }) { message ->
                MessageBubble(
                    content = message.content,
                    type = message.type,
                    mediaUrl = message.mediaUrl,
                    isCurrentUser = message.isCurrentUser,
                    isRead = message.isRead,
                    timestamp = message.timestamp,
                    showAvatar = true,
                    showName = !message.isCurrentUser && isGroup,
                    senderName = message.senderName,
                )
            }
        }

        MessageInputBar(
            messageText = messageText,
            onTextChange = { messageText = it },
            onSend = {
                // In production this hands the draft to ChatDetailViewModel,
                // which writes to Room first and syncs to Supabase after.
                if (messageText.isNotBlank()) messageText = ""
            },
            onAttach = { /* Open the attachment picker */ },
            onMic = { /* Start voice recording */ },
        )
    }
}

data class ChatMessage(
    val id: String,
    val content: String,
    val type: String,
    val isCurrentUser: Boolean,
    val senderName: String,
    val timestamp: Instant = Instant.now(),
    val mediaUrl: String? = null,
    val isRead: Boolean = true,
)

val sampleMessages = listOf(
    ChatMessage(
        id = "1",
        content = "Hey! How are you doing?",
        type = MessageType.TEXT,
        isCurrentUser = false,
        senderName = "Riya",
        timestamp = Instant.now().minusSeconds(3600),
    ),
    ChatMessage(
        id = "2",
        content = "I'm doing great! Thanks for asking \uD83D\uDE0A",
        type = MessageType.TEXT,
        isCurrentUser = true,
        senderName = "You",
        timestamp = Instant.now().minusSeconds(3500),
    ),
    ChatMessage(
        id = "3",
        content = "Did you see the new design mockups?",
        type = MessageType.TEXT,
        isCurrentUser = false,
        senderName = "Riya",
        timestamp = Instant.now().minusSeconds(3400),
    ),
    ChatMessage(
        id = "4",
        content = "Yes, they look amazing! Love the new color scheme.",
        type = MessageType.TEXT,
        isCurrentUser = true,
        senderName = "You",
        timestamp = Instant.now().minusSeconds(3300),
    ),
    ChatMessage(
        id = "5",
        content = "",
        type = MessageType.IMAGE,
        isCurrentUser = false,
        senderName = "Riya",
        timestamp = Instant.now().minusSeconds(3200),
        mediaUrl = "https://picsum.photos/400/300",
    ),
    ChatMessage(
        id = "6",
        content = "That's the one! What do you think?",
        type = MessageType.TEXT,
        isCurrentUser = false,
        senderName = "Riya",
        timestamp = Instant.now().minusSeconds(3100),
    ),
    ChatMessage(
        id = "7",
        content = "Perfect! The blue accent really pops \u2728",
        type = MessageType.TEXT,
        isCurrentUser = true,
        senderName = "You",
        timestamp = Instant.now().minusSeconds(3000),
    ),
)
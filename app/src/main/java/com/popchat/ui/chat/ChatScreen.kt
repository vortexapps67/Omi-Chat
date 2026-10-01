package com.popchat.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.ui.common.Avatar
import com.popchat.ui.common.CircleIconButton
import com.popchat.ui.common.MessageBubble
import com.popchat.ui.common.MessageInputBar
import com.popchat.ui.theme.PopChatBlue
import com.popchat.ui.theme.PopChatTheme
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    chatName: String,
    chatAvatar: String? = null,
    isGroup: Boolean = false,
    isOnline: Boolean = false,
    onBack: () -> Unit,
    onCall: () -> Unit,
    onVideoCall: () -> Unit,
    onMoreOptions: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Sample messages for demo
    val messages by remember {
        mutableStateOf(sampleMessages)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Avatar(
                        imageUrl = chatAvatar,
                        name = chatName,
                        size = 40,
                        showOnlineIndicator = !isGroup,
                        isOnline = isOnline
                    )
                    Column {
                        Text(
                            text = chatName,
                            fontSize = 16.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                        )
                        if (!isGroup) {
                            Text(
                                text = if (isOnline) "Online" else "Offline",
                                fontSize = 12.sp,
                                color = if (isOnline) com.popchat.ui.theme.PopChatGreen else PopChatTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "Group • 5 members",
                                fontSize = 12.sp,
                                color = PopChatTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = PopChatTheme.colorScheme.onSurface
                    )
                }
            },
            actions = {
                IconButton(onClick = onCall) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Call,
                        contentDescription = "Voice call",
                        tint = PopChatTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onVideoCall) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Videocam,
                        contentDescription = "Video call",
                        tint = PopChatTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onMoreOptions) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = PopChatTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = PopChatTheme.colorScheme.surfaceContainerLow
            )
        )

        // Messages List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            state = listState,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = false
        ) {
            items(messages) { message ->
                MessageBubble(
                    message = message,
                    isCurrentUser = message.isCurrentUser,
                    showAvatar = true,
                    showName = !message.isCurrentUser && isGroup,
                    senderName = message.senderName
                )
            }
        }

        // Message Input Bar
        MessageInputBar(
            messageText = messageText,
            onTextChange = { messageText = it },
            onSend = {
                if (messageText.isNotBlank()) {
                    // Add message to list
                    // In real app, this would call ViewModel
                    messageText = ""
                }
            },
            onAttach = { /* Open attachment picker */ },
            onMic = { /* Start voice recording */ },
            modifier = Modifier.padding(bottom = 0.dp)
        )
    }
}

data class ChatMessage(
    val id: String,
    val content: String,
    val type: String,
    val isCurrentUser: Boolean,
    val senderName: String,
    val timestamp: kotlinx.datetime.Instant = kotlinx.datetime.Instant.now(),
    val mediaUrl: String? = null,
    val isRead: Boolean = true
)

val sampleMessages = listOf(
    ChatMessage(
        id = "1",
        content = "Hey! How are you doing?",
        type = "text",
        isCurrentUser = false,
        senderName = "Riya",
        timestamp = kotlinx.datetime.Instant.now().minusSeconds(3600)
    ),
    ChatMessage(
        id = "2",
        content = "I'm doing great! Thanks for asking 😊",
        type = "text",
        isCurrentUser = true,
        senderName = "You",
        timestamp = kotlinx.datetime.Instant.now().minusSeconds(3500)
    ),
    ChatMessage(
        id = "3",
        content = "Did you see the new design mockups?",
        type = "text",
        isCurrentUser = false,
        senderName = "Riya",
        timestamp = kotlinx.datetime.Instant.now().minusSeconds(3400)
    ),
    ChatMessage(
        id = "4",
        content = "Yes, they look amazing! Love the new color scheme.",
        type = "text",
        isCurrentUser = true,
        senderName = "You",
        timestamp = kotlinx.datetime.Instant.now().minusSeconds(3300)
    ),
    ChatMessage(
        id = "5",
        content = "",
        type = "image",
        isCurrentUser = false,
        senderName = "Riya",
        timestamp = kotlinx.datetime.Instant.now().minusSeconds(3200),
        mediaUrl = "https://picsum.photos/400/300"
    ),
    ChatMessage(
        id = "6",
        content = "That's the one! What do you think?",
        type = "text",
        isCurrentUser = false,
        senderName = "Riya",
        timestamp = kotlinx.datetime.Instant.now().minusSeconds(3100)
    ),
    ChatMessage(
        id = "7",
        content = "Perfect! The blue accent really pops ✨",
        type = "text",
        isCurrentUser = true,
        senderName = "You",
        timestamp = kotlinx.datetime.Instant.now().minusSeconds(3000)
    )
)
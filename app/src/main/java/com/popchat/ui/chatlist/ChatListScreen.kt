package com.popchat.ui.chatlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.ui.common.Avatar
import com.popchat.ui.common.FilterChip
import com.popchat.ui.common.OutlinedInputField
import com.popchat.ui.theme.OmiChatBlue
import com.popchat.ui.theme.OmiChatTheme

@Composable
fun ChatListScreen(
    onOpenChat: (String) -> Unit,
    onNewChat: () -> Unit,
    onSearch: () -> Unit,
    onProfileClick: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(0) }
    val filters = listOf("All", "Friends", "Groups", "Channels")

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LogoMark()
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Omi Chat",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = OmiChatTheme.colorScheme.onSurface
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = OmiChatTheme.colorScheme.surfaceContainerLow
            ),
            actions = {
                // Search
                androidx.compose.material3.IconButton(onClick = onSearch) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Search,
                        contentDescription = "Search",
                        tint = OmiChatTheme.colorScheme.onSurface
                    )
                }
                // New chat
                androidx.compose.material3.IconButton(onClick = onNewChat) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.ChatBubbleOutline,
                        contentDescription = "New chat",
                        tint = OmiChatTheme.colorScheme.onSurface
                    )
                }
                // More options
                androidx.compose.material3.IconButton(onClick = { /* More options */ }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = OmiChatTheme.colorScheme.onSurface
                    )
                }
            }
        )

        // Search Bar
        OutlinedInputField(
            value = searchText,
            onValueChange = { searchText = it },
            label = "",
            placeholder = "Search chats...",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            trailingIcon = androidx.compose.material.icons.Icons.Default.Search
        )

        // Filter Pills
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 0.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        text = filter,
                        isSelected = filters.indexOf(filter) == selectedFilter,
                        onClick = { selectedFilter = filters.indexOf(filter) }
                    )
                }
            }
        }

        // Chat List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(sampleChats) { chat ->
                ChatListItem(
                    chat = chat,
                    onClick = { onOpenChat(chat.id) }
                )
                androidx.compose.material3.Divider(
                    modifier = Modifier.padding(start = 72.dp),
                    color = OmiChatTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp
                )
            }
        }
    }
}

@Composable
fun ChatListItem(
    chat: ChatItem,
    onClick: () -> Unit
) {
    val colors = OmiChatTheme.colorScheme
    val time = formatTime(chat.timestamp)

    androidx.compose.material3.ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
            .background(colors.surface),
        leading = {
            Avatar(
                imageUrl = chat.avatarUrl,
                name = chat.name,
                size = 56,
                showOnlineIndicator = !chat.isGroup,
                isOnline = chat.isOnline
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
                        text = chat.name,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                    )
                    Text(
                        text = time,
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = chat.lastMessage,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.TextOverflow.Ellipsis,
                        color = colors.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                    if (chat.unreadCount > 0) {
                        Badge(
                            badgeContent = {
                                Text(
                                    text = if (chat.unreadCount > 99) "99+" else chat.unreadCount.toString(),
                                    fontSize = 10.sp,
                                    color = colors.onErrorContainer
                                )
                            },
                            backgroundColor = OmiChatBlue,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            androidx.compose.foundation.layout.Box()
                        }
                    }
                }
            }
        },
        trailing = {
            if (chat.isMuted) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.NotificationsOff,
                    contentDescription = "Muted",
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            if (chat.isPinned) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.PushPin,
                    contentDescription = "Pinned",
                    tint = OmiChatBlue,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    )
}

data class ChatItem(
    val id: String,
    val name: String,
    val lastMessage: String,
    val timestamp: kotlinx.datetime.Instant,
    val unreadCount: Int,
    val avatarUrl: String?,
    val isGroup: Boolean,
    val isOnline: Boolean,
    val isMuted: Boolean = false,
    val isPinned: Boolean = false
)

val sampleChats = listOf(
    ChatItem(
        id = "1",
        name = "Riya Sharma",
        lastMessage = "Hey! Are we still on for tomorrow?",
        timestamp = kotlinx.datetime.Instant.now().minusSeconds(300),
        unreadCount = 2,
        avatarUrl = null,
        isGroup = false,
        isOnline = true
    ),
    ChatItem(
        id = "2",
        name = "Akshansh Sinha",
        lastMessage = "Just sent you the file 📎",
        timestamp = kotlinx.datetime.Instant.now().minusSeconds(3600),
        unreadCount = 0,
        avatarUrl = null,
        isGroup = false,
        isOnline = true
    ),
    ChatItem(
        id = "3",
        name = "Dev Team 🚀",
        lastMessage = "Riya: Meeting at 3pm today",
        timestamp = kotlinx.datetime.Instant.now().minusSeconds(7200),
        unreadCount = 5,
        avatarUrl = null,
        isGroup = true,
        isOnline = false
    ),
    ChatItem(
        id = "4",
        name = "Design Squad",
        lastMessage = "Akshansh: New mockups uploaded",
        timestamp = kotlinx.datetime.Instant.now().minusSeconds(86400),
        unreadCount = 0,
        avatarUrl = null,
        isGroup = true,
        isOnline = false
    ),
    ChatItem(
        id = "5",
        name = "Sarah Johnson",
        lastMessage = "📷 Photo",
        timestamp = kotlinx.datetime.Instant.now().minusSeconds(172800),
        unreadCount = 1,
        avatarUrl = null,
        isGroup = false,
        isOnline = false
    )
)

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

@Composable
fun LogoMark() {
    Box(
        modifier = Modifier.size(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(OmiChatBlue, androidx.compose.ui.graphics.CircleShape)
        ) {
            Text(
                text = "omi",
                fontSize = 12.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White
            )
        }
    }
}
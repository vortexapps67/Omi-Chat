package com.popchat.ui.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.toLocalDateTime
import com.popchat.ui.common.Avatar
import com.popchat.ui.common.FilterChip
import com.popchat.ui.common.OutlinedInputField
import com.popchat.ui.theme.GlassLevel
import com.popchat.ui.theme.OmiChatBlue
import com.popchat.ui.theme.glassPanel

@Composable
fun ChatListScreen(
    onOpenChat: (String) -> Unit,
    onNewChat: () -> Unit,
    onSearch: () -> Unit,
    onProfileClick: () -> Unit,
) {
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(0) }
    val filters = listOf("All", "Friends", "Groups", "Channels")

    Column(modifier = Modifier.fillMaxSize()) {
        // App bar: transparent Surface so only the glass pane shows through.
        TopAppBar(
            modifier = Modifier.glassPanel(
                shape = RoundedCornerShape(0.dp),
                level = GlassLevel.Thick,
            ),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LogoMark()
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Omi Chat",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface,
            ),
            actions = {
                IconButton(onClick = onSearch) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                IconButton(onClick = onNewChat) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "New chat",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                IconButton(onClick = onProfileClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            },
        )

        OutlinedInputField(
            value = searchText,
            onValueChange = { searchText = it },
            label = "",
            placeholder = "Search chats...",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            trailingIcon = Icons.Default.Search,
            onTrailingIconClick = onSearch,
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(filters) { filter ->
                FilterChip(
                    text = filter,
                    isSelected = filters.indexOf(filter) == selectedFilter,
                    onClick = { selectedFilter = filters.indexOf(filter) },
                )
            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        // Grouped rows: separate glass cards rather than a divided flat list, so
        // each conversation reads as a discrete pane on the mesh backdrop.
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(sampleChats, key = { it.id }) { chat ->
                ChatListItem(
                    chat = chat,
                    onClick = { onOpenChat(chat.id) },
                )
            }
        }
    }
}

@Composable
fun ChatListItem(
    chat: ChatItem,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val time = formatChatTime(chat.timestamp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassPanel(
                shape = RoundedCornerShape(22.dp),
                level = GlassLevel.Regular,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(
            imageUrl = chat.avatarUrl,
            name = chat.name,
            size = 52,
            showOnlineIndicator = !chat.isGroup,
            isOnline = chat.isOnline,
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = chat.name,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = chat.lastMessage,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colors.onSurfaceVariant,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (chat.unreadCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge(
                        containerColor = OmiChatBlue,
                        contentColor = Color.White,
                    ) {
                        Text(
                            text = if (chat.unreadCount > 99) "99+" else chat.unreadCount.toString(),
                            fontSize = 10.sp,
                        )
                    }
                }
            }
        }

        if (chat.isMuted || chat.isPinned) {
            Spacer(modifier = Modifier.width(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (chat.isPinned) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = OmiChatBlue,
                        modifier = Modifier.size(16.dp),
                    )
                }
                if (chat.isMuted) {
                    Icon(
                        imageVector = Icons.Default.NotificationsOff,
                        contentDescription = "Muted",
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
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
    val isPinned: Boolean = false,
)

val sampleChats = listOf(
    ChatItem(
        id = "1",
        name = "Riya Sharma",
        lastMessage = "Hey! Are we still on for tomorrow?",
        timestamp = kotlinx.datetime.Clock.System.now() - 300.seconds,
        unreadCount = 2,
        avatarUrl = null,
        isGroup = false,
        isOnline = true,
        isPinned = true,
    ),
    ChatItem(
        id = "2",
        name = "Akshansh Sinha",
        lastMessage = "Just sent you the file \uD83D\uDCCE",
        timestamp = kotlinx.datetime.Clock.System.now() - 3600.seconds,
        unreadCount = 0,
        avatarUrl = null,
        isGroup = false,
        isOnline = true,
    ),
    ChatItem(
        id = "3",
        name = "Dev Team \uD83D\uDE80",
        lastMessage = "Riya: Meeting at 3pm today",
        timestamp = kotlinx.datetime.Clock.System.now() - 7200.seconds,
        unreadCount = 5,
        avatarUrl = null,
        isGroup = true,
        isOnline = false,
    ),
    ChatItem(
        id = "4",
        name = "Design Squad",
        lastMessage = "Akshansh: New mockups uploaded",
        timestamp = kotlinx.datetime.Clock.System.now() - 86400.seconds,
        unreadCount = 0,
        avatarUrl = null,
        isGroup = true,
        isOnline = false,
    ),
    ChatItem(
        id = "5",
        name = "Sarah Johnson",
        lastMessage = "\uD83D\uDCF7 Photo",
        timestamp = kotlinx.datetime.Clock.System.now() - 172800.seconds,
        unreadCount = 1,
        avatarUrl = null,
        isGroup = false,
        isOnline = false,
        isMuted = true,
    ),
)

/**
 * Relative chat-list timestamp: elapsed time for the last week, then a date.
 *
 * Works in whole minutes from a Duration rather than in epoch milliseconds,
 * so it does not depend on `toEpochMilliseconds()` and the arithmetic stays
 * obvious.
 */
private fun formatChatTime(instant: kotlinx.datetime.Instant): String {
    val elapsed = kotlinx.datetime.Clock.System.now() - instant

    val minutes = elapsed.inWholeMinutes
    val hours = elapsed.inWholeHours
    val days = elapsed.inWholeDays

    return when {
        minutes < 1L -> "now"
        minutes < 60L -> "${minutes}m"
        hours < 24L -> "${hours}h"
        days < 7L -> "${days}d"
        else -> instant
            .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
            .date
            .toString()
    }
}

/** The app mark: a frosted disc rather than a flat brand circle. */
@Composable
fun LogoMark() {
    Box(
        modifier = Modifier
            .size(34.dp)
            .glassPanel(
                shape = CircleShape,
                level = GlassLevel.Thick,
                accent = OmiChatBlue,
            )
            .background(OmiChatBlue.copy(alpha = 0.28f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "omi",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
    }
}
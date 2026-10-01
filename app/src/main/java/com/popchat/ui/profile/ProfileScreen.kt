package com.popchat.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.ui.common.Avatar
import com.popchat.ui.common.OutlinedPillButton
import com.popchat.ui.common.PillButton
import com.popchat.ui.theme.OmiChatBlue
import com.popchat.ui.theme.OmiChatGreen
import com.popchat.ui.theme.OmiChatTheme

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onSettingsClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Profile", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium) },
            navigationIcon = {
                androidx.compose.material3.IconButton(onClick = onBack) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = OmiChatTheme.colorScheme.surfaceContainerLow
            )
        )

        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Profile Header
            item {
                ProfileHeader(
                    name = "Akshansh Sinha",
                    handle = "@akshansh",
                    bio = "Building cool stuff 🚀 | Developer | Coffee addict",
                    avatarUrl = null,
                    stats = ProfileStats(posts = 124, friends = 567, followers = 2341),
                    onEditClick = onEditProfile
                )
            }

            // Settings sections
            item {
                SettingsSection(
                    title = "Account",
                    items = listOf(
                        SettingsItem("Chat Settings", androidx.compose.material.icons.Icons.Default.ChatBubbleOutline) { onSettingsClick("chat") },
                        SettingsItem("Privacy & Security", androidx.compose.material.icons.Icons.Default.Security) { onSettingsClick("privacy") },
                        SettingsItem("Notifications", androidx.compose.material.icons.Icons.Default.Notifications) { onSettingsClick("notifications") },
                        SettingsItem("Appearance", androidx.compose.material.icons.Icons.Default.Palette) { onSettingsClick("appearance") }
                    )
                )
            }

            item {
                SettingsSection(
                    title = "Support",
                    items = listOf(
                        SettingsItem("Help & Support", androidx.compose.material.icons.Icons.Default.Help) { onSettingsClick("help") },
                        SettingsItem("About Omi Chat", androidx.compose.material.icons.Icons.Default.Info) { onSettingsClick("about") }
                    )
                )
            }

            // Logout
            item {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    PillButton(
                        text = "Log Out",
                        onClick = { onSettingsClick("logout") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    name: String,
    handle: String,
    bio: String,
    avatarUrl: String?,
    stats: ProfileStats,
    onEditClick: () -> Unit
) {
    val colors = OmiChatTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Avatar with accent ring
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            // Accent ring
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .background(Color.Transparent, CircleShape)
                    .border(3.dp, OmiChatBlue, CircleShape)
            )
            
            Avatar(
                imageUrl = avatarUrl,
                name = name,
                size = 100
            )
            
            // Verified badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(4.dp)
            ) {
                Badge(
                    badgeContent = {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    backgroundColor = OmiChatBlue,
                    modifier = Modifier.size(24.dp)
                ) {
                    androidx.compose.foundation.layout.Box()
                }
            }
        }

        // Name
        Text(
            text = name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface
        )

        // Handle
        Text(
            text = handle,
            fontSize = 16.sp,
            color = colors.onSurfaceVariant
        )

        // Bio
        Text(
            text = bio,
            fontSize = 14.sp,
            color = colors.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.TextAlign.Center,
            maxLines = 3,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        // Stats
        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            StatColumn(count = stats.posts, label = "Posts")
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(colors.outlineVariant)
                    .padding(horizontal = 24.dp)
            )
            StatColumn(count = stats.friends, label = "Friends")
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(40.dp)
                    .background(colors.outlineVariant)
                    .padding(horizontal = 24.dp)
            )
            StatColumn(count = stats.followers, label = "Followers")
        }

        // Edit Profile Button
        OutlinedPillButton(
            text = "Edit Profile",
            onClick = onEditClick,
            modifier = Modifier.width(200.dp)
        )
    }
}

@Composable
fun StatColumn(count: Int, label: String) {
    val colors = OmiChatTheme.colorScheme
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = formatCount(count),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = colors.onSurfaceVariant
        )
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1000000 -> "${count / 1000000}M"
        count >= 1000 -> "${count / 1000}K"
        else -> count.toString()
    }
}

data class ProfileStats(
    val posts: Int,
    val friends: Int,
    val followers: Int
)

data class SettingsItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit,
    val trailing: (@Composable () -> Unit)? = null
)

@Composable
fun SettingsSection(
    title: String,
    items: List<SettingsItem>
) {
    val colors = OmiChatTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Section title
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = colors.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )

        // Items
        items.forEachIndexed { index, item ->
            val isLast = index == items.lastIndex
            androidx.compose.material3.ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(colors.surface)
                    .clickable(onClick = item.onClick),
                leading = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant
                    )
                },
                headlineContent = {
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        color = colors.onSurface
                    )
                },
                trailing = item.trailing ?? {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            )
            
            if (!isLast) {
                androidx.compose.material3.Divider(
                    modifier = Modifier.padding(start = 72.dp),
                    color = colors.outlineVariant,
                    thickness = 0.5.dp
                )
            }
        }
    }
}
package com.popchat.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.ui.common.Avatar
import com.popchat.ui.common.OutlinedPillButton
import com.popchat.ui.common.PillButton
import com.popchat.ui.theme.GlassLevel
import com.popchat.ui.theme.OmiChatBlue
import com.popchat.ui.theme.glassPanel

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onSettingsClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
    ) {
        TopAppBar(
            modifier = Modifier.glassPanel(
                shape = RoundedCornerShape(0.dp),
                level = GlassLevel.Thick,
            ),
            title = {
                Text(
                    text = "Profile",
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
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
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                ProfileHeader(
                    name = "Akshansh Sinha",
                    handle = "@akshansh",
                    bio = "Building cool stuff | Developer | Coffee addict",
                    avatarUrl = null,
                    stats = ProfileStats(posts = 124, friends = 567, followers = 2341),
                    onEditClick = onEditProfile,
                )
            }

            item {
                ProfileSettingsSection(
                    title = "Account",
                    items = listOf(
                        SettingsItem("Chat Settings", Icons.Default.ChatBubbleOutline) {
                            onSettingsClick("chat")
                        },
                        SettingsItem("Privacy & Security", Icons.Default.Security) {
                            onSettingsClick("privacy")
                        },
                        SettingsItem("Notifications", Icons.Default.Notifications) {
                            onSettingsClick("notifications")
                        },
                        SettingsItem("Appearance", Icons.Default.Palette) {
                            onSettingsClick("appearance")
                        },
                    ),
                )
            }

            item {
                ProfileSettingsSection(
                    title = "Support",
                    items = listOf(
                        SettingsItem("Help & Support", Icons.Default.Help) {
                            onSettingsClick("help")
                        },
                        SettingsItem("About Omi Chat", Icons.Default.Info) {
                            onSettingsClick("about")
                        },
                    ),
                )
            }

            item {
                PillButton(
                    text = "Log Out",
                    onClick = { onSettingsClick("logout") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }
        }
    }
}

/**
 * Profile identity block.
 *
 * The whole header sits on one thick glass pane. Name, handle and bio need a
 * calm local surface because the mesh backdrop behind them is saturated enough
 * to make small text shimmer.
 */
@Composable
fun ProfileHeader(
    name: String,
    handle: String,
    bio: String,
    avatarUrl: String?,
    stats: ProfileStats,
    onEditClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .glassPanel(
                shape = RoundedCornerShape(28.dp),
                level = GlassLevel.Thick,
            )
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier.size(108.dp),
            contentAlignment = Alignment.Center,
        ) {
            // Glass halo instead of a flat accent ring — reads as the avatar
            // sitting behind a lens rather than wearing a hoop.
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .glassPanel(
                        shape = CircleShape,
                        level = GlassLevel.Thick,
                        accent = OmiChatBlue,
                    ),
            )

            Avatar(
                imageUrl = avatarUrl,
                name = name,
                size = 92,
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(26.dp)
                    .glassPanel(
                        shape = CircleShape,
                        level = GlassLevel.Thick,
                        accent = OmiChatBlue,
                    )
                    .border(2.dp, OmiChatBlue, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Verified",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp),
                )
            }
        }

        Text(
            text = name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface,
        )

        Text(
            text = handle,
            fontSize = 16.sp,
            color = colors.onSurfaceVariant,
        )

        Text(
            text = bio,
            fontSize = 14.sp,
            color = colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 3,
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            StatColumn(count = stats.posts, label = "Posts")
            StatDivider()
            StatColumn(count = stats.friends, label = "Friends")
            StatDivider()
            StatColumn(count = stats.followers, label = "Followers")
        }

        OutlinedPillButton(
            text = "Edit Profile",
            onClick = onEditClick,
            modifier = Modifier.width(200.dp),
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .width(1.dp)
            .height(36.dp)
            .background(MaterialTheme.colorScheme.outlineVariant),
    )
}

@Composable
private fun StatColumn(count: Int, label: String) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = formatCount(count),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface,
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = colors.onSurfaceVariant,
        )
    }
}

private fun formatCount(count: Int): String = when {
    count >= 1_000_000 -> "${count / 1_000_000}M"
    count >= 1_000 -> "${count / 1_000}K"
    else -> count.toString()
}

data class ProfileStats(
    val posts: Int,
    val friends: Int,
    val followers: Int,
)

data class SettingsItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val trailing: (@Composable () -> Unit)? = null,
)

/**
 * Grouped profile settings block on a single frosted pane.
 *
 * Named `ProfileSettingsSection` because the full Settings screen has its own
 * `SettingsSection` in `com.popchat.ui.settings` — same look, different call
 * sites, and keeping them separate stops the two from drifting apart.
 */
@Composable
fun ProfileSettingsSection(
    title: String,
    items: List<SettingsItem>,
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .glassPanel(
                    shape = RoundedCornerShape(20.dp),
                    level = GlassLevel.Regular,
                )
                .padding(vertical = 4.dp),
        ) {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = item.onClick)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        color = colors.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    item.trailing?.invoke()
                        ?: Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant.copy(alpha = 0.45f),
                            modifier = Modifier.size(20.dp),
                        )
                }

                if (index != items.lastIndex) {
                    Spacer(
                        modifier = Modifier
                            .padding(start = 54.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(colors.outlineVariant.copy(alpha = 0.6f)),
                    )
                }
            }
        }
    }
}

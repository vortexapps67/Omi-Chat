package com.popchat.ui.discover

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.ui.common.Avatar
import com.popchat.ui.common.FilterChip
import com.popchat.ui.theme.GlassLevel
import com.popchat.ui.theme.OmiChatBlue
import com.popchat.ui.theme.glassPanel

@Composable
fun DiscoverScreen(
    onBack: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("People", "Groups", "Channels")

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
                    text = "Discover",
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
            actions = {
                IconButton(onClick = { /* Focus the discover search field */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            tabs.forEachIndexed { index, tab ->
                FilterChip(
                    text = tab,
                    isSelected = index == selectedTab,
                    onClick = { selectedTab = index },
                )
            }
        }

        when (selectedTab) {
            0 -> PeopleGrid()
            1 -> GroupsList()
            else -> ChannelsList()
        }
    }
}

@Composable
fun PeopleGrid() {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(samplePeople) { person ->
            PersonCard(person = person)
        }
    }
}

@Composable
private fun PersonCard(person: Person) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassPanel(
                shape = RoundedCornerShape(24.dp),
                level = GlassLevel.Regular,
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Avatar(
            imageUrl = person.avatarUrl,
            name = person.name,
            size = 72,
            showOnlineIndicator = true,
            isOnline = person.isOnline,
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = person.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface,
                )
                if (person.isVerified) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = OmiChatBlue,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
            Text(
                text = person.bio,
                fontSize = 12.sp,
                color = colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }

        FollowButton(
            isFollowing = person.isFollowing,
            onClick = { /* Toggle follow */ },
        )
    }
}

/**
 * Follow / Following toggle.
 *
 * Unfollowed state is a frosted pill so it stays visually quiet beside the
 * solid brand fill of the followed state.
 */
@Composable
private fun FollowButton(isFollowing: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isFollowing) {
                    Modifier.glassPanel(
                        shape = RoundedCornerShape(20.dp),
                        level = GlassLevel.Thin,
                        accent = OmiChatBlue,
                    )
                } else {
                    Modifier.glassPanel(
                        shape = RoundedCornerShape(20.dp),
                        level = GlassLevel.Thin,
                    )
                },
            )
            .background(
                color = if (isFollowing) {
                    OmiChatBlue.copy(alpha = 0.20f)
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(20.dp),
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (isFollowing) "Following" else "Follow",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isFollowing) OmiChatBlue else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun GroupsList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(sampleGroups) { group ->
            GroupCard(group = group)
        }
    }
}

@Composable
private fun GroupCard(group: Group) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassPanel(
                shape = RoundedCornerShape(22.dp),
                level = GlassLevel.Regular,
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(
            imageUrl = group.avatarUrl,
            name = group.name,
            size = 56,
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = group.name,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (group.isPrivate) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Private",
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
            Text(
                text = group.description,
                fontSize = 12.sp,
                color = colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${group.memberCount} members",
                fontSize = 12.sp,
                color = colors.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        CompactActionButton(
            label = if (group.isJoined) "Joined" else "Join",
            active = group.isJoined,
            onClick = { /* Toggle join */ },
        )
    }
}

@Composable
fun ChannelsList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(sampleChannels) { channel ->
            ChannelCard(channel = channel)
        }
    }
}

@Composable
private fun ChannelCard(channel: Channel) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassPanel(
                shape = RoundedCornerShape(22.dp),
                level = GlassLevel.Regular,
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(
            imageUrl = channel.avatarUrl,
            name = channel.name,
            size = 56,
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = channel.name,
                    fontWeight = FontWeight.Medium,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(modifier = Modifier.size(8.dp))
                CategoryPill(label = channel.category)
            }
            Text(
                text = channel.description,
                fontSize = 12.sp,
                color = colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${channel.subscriberCount} subscribers",
                fontSize = 12.sp,
                color = colors.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        CompactActionButton(
            label = if (channel.isSubscribed) "Subscribed" else "Subscribe",
            active = channel.isSubscribed,
            onClick = { /* Toggle subscribe */ },
        )
    }
}

/** Frosted capsule for the channel category. */
@Composable
private fun CategoryPill(label: String) {
    Box(
        modifier = Modifier
            .glassPanel(
                shape = RoundedCornerShape(8.dp),
                level = GlassLevel.Thin,
                accent = OmiChatBlue.copy(alpha = 0.18f),
            )
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

/** Small frosted join/subscribe button sized for a list row. */
@Composable
private fun CompactActionButton(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .width(if (label.length > 6) 96.dp else 72.dp)
            .height(34.dp)
            .glassPanel(
                shape = RoundedCornerShape(17.dp),
                level = GlassLevel.Thin,
                accent = if (active) OmiChatBlue.copy(alpha = 0.22f) else Color.Unspecified,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (active) OmiChatBlue else MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
        )
    }
}

data class Person(
    val name: String,
    val bio: String,
    val avatarUrl: String?,
    val isOnline: Boolean,
    val isVerified: Boolean = false,
    val isFollowing: Boolean = false,
)

data class Group(
    val name: String,
    val description: String,
    val avatarUrl: String?,
    val memberCount: Int,
    val isPrivate: Boolean = false,
    val isJoined: Boolean = false,
)

data class Channel(
    val name: String,
    val description: String,
    val avatarUrl: String?,
    val subscriberCount: Int,
    val category: String,
    val isSubscribed: Boolean = false,
)

val samplePeople = listOf(
    Person("Alex Chen", "iOS Developer - Swift enthusiast", null, true, true, false),
    Person("Maria Garcia", "UI/UX Designer - Figma expert", null, true, false, true),
    Person("James Wilson", "Backend Engineer - Go & Rust", null, false, false, false),
    Person("Sarah Kim", "Product Manager - Ex-Google", null, true, true, false),
    Person("David Park", "Full Stack - React & Kotlin", null, true, false, false),
    Person("Lisa Thompson", "DevOps Engineer - Kubernetes", null, false, false, false),
)

val sampleGroups = listOf(
    Group("Kotlin Developers", "All things Kotlin, Coroutines, and Compose", null, 12450, false, true),
    Group("Jetpack Compose Community", "Share tips, tricks, and showcase your Compose UI", null, 8932, false, false),
    Group("Android Architecture", "Clean Architecture, MVI, MVVM discussions", null, 5671, true, false),
    Group("Indie App Developers", "Building and launching indie apps", null, 3421, false, true),
)

val sampleChannels = listOf(
    Channel("Android Weekly", "Latest Android news, articles, and tutorials", null, 125000, "Technology", true),
    Channel("Material Design", "Official Material Design updates and guidelines", null, 89000, "Design", false),
    Channel("Kotlin Lang", "Kotlin language updates and best practices", null, 67000, "Technology", true),
    Channel("Compose Camp", "Jetpack Compose tutorials and showcases", null, 45000, "Technology", false),
)

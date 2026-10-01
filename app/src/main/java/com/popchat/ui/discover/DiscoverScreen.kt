package com.popchat.ui.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.ui.common.Avatar
import com.popchat.ui.common.PillButton
import com.popchat.ui.theme.OmiChatBlue
import com.popchat.ui.theme.OmiChatTheme

@Composable
fun DiscoverScreen(
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("People", "Groups", "Channels")

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        TopAppBar(
            title = { Text("Discover") },
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
            ),
            actions = {
                androidx.compose.material3.IconButton(onClick = { /* Search */ }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            }
        )

        // Tab bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEach { tab ->
                FilterChip(
                    text = tab,
                    isSelected = tabs.indexOf(tab) == selectedTab,
                    onClick = { selectedTab = tabs.indexOf(tab) }
                )
            }
        }

        // Content
        when (selectedTab) {
            0 -> PeopleGrid()
            1 -> GroupsList()
            2 -> ChannelsList()
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = OmiChatTheme.colorScheme
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
            color = if (isSelected) androidx.compose.ui.graphics.Color.White else colors.onSurfaceVariant
        )
    }
}

@Composable
fun PeopleGrid() {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        cells = GridCells.Fixed(2),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(samplePeople) { person ->
            PersonCard(person = person)
        }
    }
}

@Composable
fun PersonCard(person: Person) {
    val colors = OmiChatTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface, RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Avatar(
            imageUrl = person.avatarUrl,
            name = person.name,
            size = 80,
            showOnlineIndicator = true,
            isOnline = person.isOnline
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = person.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (person.isVerified) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = OmiChatBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                text = person.bio,
                fontSize = 12.sp,
                color = colors.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.TextAlign.Center,
                maxLines = 2
            )
        }

        PillButton(
            text = if (person.isFollowing) "Following" else "Follow",
            onClick = { /* Toggle follow */ },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun GroupsList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp, 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(sampleGroups) { group ->
            GroupCard(group = group)
        }
    }
}

@Composable
fun GroupCard(group: Group) {
    val colors = OmiChatTheme.colorScheme
    androidx.compose.material3.ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface, RoundedCornerShape(16.dp))
            .padding(8.dp),
        leading = {
            Avatar(
                imageUrl = group.avatarUrl,
                name = group.name,
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
                    Text(text = group.name, fontWeight = FontWeight.Medium)
                    if (group.isPrivate) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Lock,
                            contentDescription = "Private",
                            tint = colors.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(text = group.description, fontSize = 12.sp, color = colors.onSurfaceVariant, maxLines = 1, overflow = androidx.compose.ui.text.TextOverflow.Ellipsis)
                Text(text = "${group.memberCount} members", fontSize = 12.sp, color = colors.onSurfaceVariant)
            }
        },
        trailing = {
            PillButton(
                text = if (group.isJoined) "Joined" else "Join",
                onClick = { /* Toggle join */ },
                modifier = Modifier.width(80.dp).height(32.dp)
            )
        }
    )
}

@Composable
fun ChannelsList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp, 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(sampleChannels) { channel ->
            ChannelCard(channel = channel)
        }
    }
}

@Composable
fun ChannelCard(channel: Channel) {
    val colors = OmiChatTheme.colorScheme
    androidx.compose.material3.ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface, RoundedCornerShape(16.dp))
            .padding(8.dp),
        leading = {
            Avatar(
                imageUrl = channel.avatarUrl,
                name = channel.name,
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
                    Text(text = channel.name, fontWeight = FontWeight.Medium)
                    Badge(
                        badgeContent = { Text(text = channel.category, fontSize = 10.sp, color = colors.onPrimaryContainer) },
                        backgroundColor = OmiChatBlue.copy(alpha = 0.2f)
                    ) { Box() }
                }
                Text(text = channel.description, fontSize = 12.sp, color = colors.onSurfaceVariant, maxLines = 1, overflow = androidx.compose.ui.text.TextOverflow.Ellipsis)
                Text(text = "${channel.subscriberCount} subscribers", fontSize = 12.sp, color = colors.onSurfaceVariant)
            }
        },
        trailing = {
            PillButton(
                text = if (channel.isSubscribed) "Subscribed" else "Subscribe",
                onClick = { /* Toggle subscribe */ },
                modifier = Modifier.width(100.dp).height(32.dp)
            )
        }
    )
}

@Composable
fun Badge(
    badgeContent: @Composable () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        badgeContent()
    }
}

data class Person(
    val name: String,
    val bio: String,
    val avatarUrl: String?,
    val isOnline: Boolean,
    val isVerified: Boolean = false,
    val isFollowing: Boolean = false
)

data class Group(
    val name: String,
    val description: String,
    val avatarUrl: String?,
    val memberCount: Int,
    val isPrivate: Boolean = false,
    val isJoined: Boolean = false
)

data class Channel(
    val name: String,
    val description: String,
    val avatarUrl: String?,
    val subscriberCount: Int,
    val category: String,
    val isSubscribed: Boolean = false
)

val samplePeople = listOf(
    Person("Alex Chen", "iOS Developer • Swift enthusiast", null, true, true, false),
    Person("Maria Garcia", "UI/UX Designer • Figma expert", null, true, false, true),
    Person("James Wilson", "Backend Engineer • Go & Rust", null, false, false, false),
    Person("Sarah Kim", "Product Manager • Ex-Google", null, true, true, false),
    Person("David Park", "Full Stack • React & Kotlin", null, true, false, false),
    Person("Lisa Thompson", "DevOps Engineer • Kubernetes", null, false, false, false)
)

val sampleGroups = listOf(
    Group("Kotlin Developers", "All things Kotlin, Coroutines, and Compose", null, 12450, false, true),
    Group("Jetpack Compose Community", "Share tips, tricks, and showcase your Compose UI", null, 8932, false, false),
    Group("Android Architecture", "Clean Architecture, MVI, MVVM discussions", null, 5671, true, false),
    Group("Indie App Developers", "Building and launching indie apps", null, 3421, false, true)
)

val sampleChannels = listOf(
    Channel("Android Weekly", "Latest Android news, articles, and tutorials", null, 125000, "Technology", true),
    Channel("Material Design", "Official Material Design updates and guidelines", null, 89000, "Design", false),
    Channel("Kotlin Lang", "Kotlin language updates and best practices", null, 67000, "Technology", true),
    Channel("Compose Camp", "Jetpack Compose tutorials and showcases", null, 45000, "Technology", false)
)
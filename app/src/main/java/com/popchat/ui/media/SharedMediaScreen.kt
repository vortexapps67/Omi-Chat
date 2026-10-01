package com.popchat.ui.media

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
import com.popchat.ui.theme.PopChatBlue
import com.popchat.ui.theme.PopChatTheme

@Composable
fun SharedMediaScreen(
    chatName: String,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Media", "Files", "Links")

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        TopAppBar(
            title = { Text("Shared Media") },
            navigationIcon = {
                androidx.compose.material3.IconButton(onClick = onBack) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = PopChatTheme.colorScheme.surfaceContainerLow
            )
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
            0 -> MediaGrid()
            1 -> FilesList()
            2 -> LinksList()
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = PopChatTheme.colorScheme
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(
                color = if (isSelected) PopChatBlue else colors.surfaceContainerHighest,
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
fun MediaGrid() {
    val mediaItems = sampleMediaItems

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        cells = GridCells.Fixed(3),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(mediaItems) { item ->
            MediaGridItem(item = item)
        }
        // Add "Upload" card at the end
        item {
            UploadCard()
        }
    }
}

@Composable
fun MediaGridItem(item: MediaItem) {
    val colors = PopChatTheme.colorScheme
    val aspectRatio = if (item.isVideo) 16f / 9f else 1f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(androidx.compose.ui.graphics.RoundedCornerShape(12.dp))
    ) {
        io.coil.compose.AsyncImage(
            model = item.thumbnailUrl,
            contentDescription = item.type,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Video duration badge
        if (item.isVideo) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = item.duration,
                        fontSize = 10.sp,
                        color = androidx.compose.ui.graphics.Color.White
                    )
                }
            }
        }

        // Video play indicator
        if (item.isVideo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f), androidx.compose.ui.graphics.RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.PlayCircleFill,
                    contentDescription = "Play video",
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}

@Composable
fun UploadCard() {
    val colors = PopChatTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(colors.surfaceContainerHighest, androidx.compose.ui.graphics.RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                androidx.compose.foundation.gestures.detectTapGestures(onTap = { /* Open picker */ })
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Add,
                contentDescription = "Add media",
                tint = colors.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = "Add Media",
                fontSize = 12.sp,
                color = colors.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FilesList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp, 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sampleFiles) { file ->
            FileListItem(file = file)
        }
    }
}

@Composable
fun FileListItem(file: FileItem) {
    val colors = PopChatTheme.colorScheme
    androidx.compose.material3.ListItem(
        modifier = Modifier.fillMaxWidth().background(colors.surface, RoundedCornerShape(12.dp)).padding(8.dp),
        leading = {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = file.icon,
                    contentDescription = null,
                    tint = PopChatBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        headlineContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(text = file.name, fontWeight = FontWeight.Medium, maxLines = 1, overflow = androidx.compose.ui.text.TextOverflow.Ellipsis)
                Text(text = file.size, fontSize = 12.sp, color = colors.onSurfaceVariant)
            }
        },
        trailing = {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Download,
                contentDescription = "Download",
                tint = PopChatBlue
            )
        }
    )
}

@Composable
fun LinksList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp, 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sampleLinks) { link ->
            LinkListItem(link = link)
        }
    }
}

@Composable
fun LinkListItem(link: LinkItem) {
    val colors = PopChatTheme.colorScheme
    androidx.compose.material3.ListItem(
        modifier = Modifier.fillMaxWidth().background(colors.surface, RoundedCornerShape(12.dp)).padding(8.dp),
        leading = {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                io.coil.compose.AsyncImage(
                    model = link.faviconUrl,
                    contentDescription = "Favicon",
                    modifier = Modifier.size(32.dp).clip(androidx.compose.ui.graphics.RoundedCornerShape(8.dp)),
                    placeholder = {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Link,
                            contentDescription = "Link",
                            tint = PopChatBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                )
            }
        },
        headlineContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(text = link.title, fontWeight = FontWeight.Medium, maxLines = 1, overflow = androidx.compose.ui.text.TextOverflow.Ellipsis)
                Text(text = link.url, fontSize = 12.sp, color = colors.onSurfaceVariant, maxLines = 1, overflow = androidx.compose.ui.text.TextOverflow.Ellipsis)
            }
        }
    )
}

data class MediaItem(
    val id: String,
    val thumbnailUrl: String,
    val isVideo: Boolean,
    val duration: String,
    val type: String
)

data class FileItem(
    val name: String,
    val size: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class LinkItem(
    val title: String,
    val url: String,
    val faviconUrl: String?
)

val sampleMediaItems = listOf(
    MediaItem("1", "https://picsum.photos/400/400", false, "", "image"),
    MediaItem("2", "https://picsum.photos/400/300", true, "0:45", "video"),
    MediaItem("3", "https://picsum.photos/400/400", false, "", "image"),
    MediaItem("4", "https://picsum.photos/400/300", true, "1:23", "video"),
    MediaItem("5", "https://picsum.photos/400/400", false, "", "image"),
    MediaItem("6", "https://picsum.photos/400/300", true, "2:10", "video"),
    MediaItem("7", "https://picsum.photos/400/400", false, "", "image"),
    MediaItem("8", "https://picsum.photos/400/400", false, "", "image"),
    MediaItem("9", "https://picsum.photos/400/300", true, "0:30", "video")
)

val sampleFiles = listOf(
    FileItem("Project_Proposal.pdf", "2.4 MB", androidx.compose.material.icons.Icons.Default.PictureAsPdf),
    FileItem("Design_Specs.fig", "15.7 MB", androidx.compose.material.icons.Icons.Default.Image),
    FileItem("Meeting_Notes.docx", "512 KB", androidx.compose.material.icons.Icons.Default.Description),
    FileItem("Budget_2024.xlsx", "1.2 MB", androidx.compose.material.icons.Icons.Default.TableChart),
    FileItem("App_Icon.png", "245 KB", androidx.compose.material.icons.Icons.Default.Image)
)

val sampleLinks = listOf(
    LinkItem("GitHub Repository", "github.com/popchat/app", "https://github.githubassets.com/favicon.ico"),
    LinkItem("Design System - Figma", "figma.com/file/abc123", "https://static.figma.com/app/icon/1.png"),
    LinkItem("API Documentation", "docs.popchat.app/api", "https://docs.popchat.app/favicon.ico"),
    LinkItem("Team Notion Workspace", "notion.so/popchat", "https://notion.so/images/favicon.ico")
)
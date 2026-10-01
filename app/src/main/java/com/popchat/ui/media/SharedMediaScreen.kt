package com.popchat.ui.media

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.TableChart
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.popchat.ui.common.FilterChip
import com.popchat.ui.theme.GlassLevel
import com.popchat.ui.theme.OmiChatBlue
import com.popchat.ui.theme.glassPanel

@Composable
fun SharedMediaScreen(
    chatName: String,
    onBack: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Media", "Files", "Links")

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
                Column {
                    Text(
                        text = "Shared Media",
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = chatName,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
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
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
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
            0 -> MediaGrid()
            1 -> FilesList()
            else -> LinksList()
        }
    }
}

@Composable
fun MediaGrid() {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(sampleMediaItems) { item ->
            MediaGridItem(item = item)
        }
        // item() is a LazyGridScope member, so it needs no import.
        item {
            UploadCard()
        }
    }
}

/**
 * A single media thumbnail.
 *
 * Thumbnails stay opaque — glass behind a photo just muddies it. The glass is
 * reserved for the *chrome* layered on top: the duration chip and the play
 * scrim, which need to stay readable over arbitrary image content.
 */
@Composable
fun MediaGridItem(item: MediaItem) {
    val aspectRatio = if (item.isVideo) 16f / 9f else 1f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(12.dp)),
    ) {
        AsyncImage(
            model = item.thumbnailUrl,
            contentDescription = item.type,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        if (item.isVideo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = "Play video",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp),
                )
            }
        }

        if (item.isVideo && item.duration.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .glassPanel(
                        shape = RoundedCornerShape(6.dp),
                        level = GlassLevel.Thick,
                        accent = Color.Black.copy(alpha = 0.45f),
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    text = item.duration,
                    fontSize = 10.sp,
                    color = Color.White,
                )
            }
        }
    }
}

@Composable
fun UploadCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .glassPanel(
                shape = RoundedCornerShape(12.dp),
                level = GlassLevel.Thin,
            )
            .clickable { /* Open the system picker */ },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add media",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp),
            )
            Text(
                text = "Add",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun FilesList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(sampleFiles) { file ->
            FileListItem(file = file)
        }
    }
}

@Composable
private fun FileListItem(file: FileItem) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassPanel(
                shape = RoundedCornerShape(16.dp),
                level = GlassLevel.Regular,
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Frosted icon tile rather than a flat brand square.
        Box(
            modifier = Modifier
                .size(46.dp)
                .glassPanel(
                    shape = RoundedCornerShape(12.dp),
                    level = GlassLevel.Thin,
                    accent = OmiChatBlue,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = file.icon,
                contentDescription = null,
                tint = OmiChatBlue,
                modifier = Modifier.size(22.dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = file.name,
                fontWeight = FontWeight.Medium,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = file.size,
                fontSize = 12.sp,
                color = colors.onSurfaceVariant,
            )
        }

        Icon(
            imageVector = Icons.Default.Download,
            contentDescription = "Download",
            tint = colors.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
fun LinksList() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(sampleLinks) { link ->
            LinkListItem(link = link)
        }
    }
}

@Composable
private fun LinkListItem(link: LinkItem) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassPanel(
                shape = RoundedCornerShape(16.dp),
                level = GlassLevel.Regular,
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = link.faviconUrl,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .glassPanel(shape = RoundedCornerShape(10.dp), level = GlassLevel.Thin),
            contentScale = ContentScale.Fit,
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = link.title,
                fontWeight = FontWeight.Medium,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = link.url,
                fontSize = 12.sp,
                color = colors.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Icon(
            imageVector = Icons.Default.Link,
            contentDescription = null,
            tint = colors.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
    }
}

data class MediaItem(
    val id: String,
    val thumbnailUrl: String,
    val isVideo: Boolean,
    val duration: String,
    val type: String,
)

data class FileItem(
    val name: String,
    val size: String,
    val icon: ImageVector,
)

data class LinkItem(
    val title: String,
    val url: String,
    val faviconUrl: String?,
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
    MediaItem("9", "https://picsum.photos/400/300", true, "0:30", "video"),
)

val sampleFiles = listOf(
    FileItem("Project_Proposal.pdf", "2.4 MB", Icons.Default.PictureAsPdf),
    FileItem("Design_Specs.fig", "15.7 MB", Icons.Default.Image),
    FileItem("Meeting_Notes.docx", "512 KB", Icons.Default.Description),
    FileItem("Budget_2024.xlsx", "1.2 MB", Icons.Default.TableChart),
    FileItem("App_Icon.png", "245 KB", Icons.Default.Image),
)

val sampleLinks = listOf(
    LinkItem("GitHub Repository", "github.com/vortexapps67/Omi-Chat", "https://github.githubassets.com/favicon.ico"),
    LinkItem("Design System - Figma", "figma.com/file/abc123", "https://static.figma.com/app/icon/1.png"),
    LinkItem("API Documentation", "docs.omichat.app/api", "https://docs.omichat.app/favicon.ico"),
    LinkItem("Team Notion Workspace", "notion.so/omichat", "https://notion.so/images/favicon.ico"),
)

package com.popchat.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.ui.theme.PopChatBlue
import com.popchat.ui.theme.PopChatGreen
import io.coil.compose.AsyncImage
import io.coil.compose.rememberAsyncImagePainter

@Composable
fun Avatar(
    imageUrl: String? = null,
    name: String? = null,
    modifier: Modifier = Modifier,
    size: Int = 48,
    showOnlineIndicator: Boolean = false,
    isOnline: Boolean = false
) {
    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background circle with initial or image
        if (imageUrl != null && imageUrl.isNotBlank()) {
            val painter = rememberAsyncImagePainter(imageUrl)
            AsyncImage(
                model = painter,
                contentDescription = "Avatar for ${name ?: "user"}",
                modifier = Modifier.size(size.dp).clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = { drawInitials(name, size) }
            )
        } else {
            drawInitials(name, size)
        }

        // Online indicator
        if (showOnlineIndicator && isOnline) {
            Box(
                modifier = Modifier
                    .size((size * 0.3).toInt().dp)
                    .align(Alignment.BottomEnd)
                    .background(PopChatGreen, CircleShape)
                    .border(3.dp, Color.White, CircleShape)
            )
        }
    }
}

@Composable
private fun drawInitials(name: String?, size: Int) {
    val initials = name?.split(" ")?.map { it.first().uppercase() }?.joinToString("")?.take(2) ?: "?"
    androidx.compose.foundation.text.BasicText(
        text = androidx.compose.ui.text.AnnotatedString(initials),
        fontSize = (size * 0.4).sp,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
        color = androidx.compose.ui.graphics.Color.White,
        modifier = Modifier.size(size.dp).background(PopChatBlue).clip(CircleShape),
        textAlign = androidx.compose.ui.text.TextAlign.Center,
        style = androidx.compose.ui.text.TextStyle(
            fontSize = (size * 0.4).sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = androidx.compose.ui.graphics.Color.White
        )
    )
}

@Composable
fun StatusAvatar(
    imageUrl: String? = null,
    name: String? = null,
    modifier: Modifier = Modifier,
    size: Int = 48,
    status: String = "online"
) {
    val isOnline = status == "online"
    Avatar(
        imageUrl = imageUrl,
        name = name,
        modifier = modifier,
        size = size,
        showOnlineIndicator = true,
        isOnline = isOnline
    )
}
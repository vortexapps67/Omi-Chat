package com.popchat.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.ui.common.PillButton
import com.popchat.ui.common.OutlinedPillButton
import com.popchat.ui.common.TextButton
import com.popchat.ui.theme.PopChatBlue
import com.popchat.ui.theme.PopChatGreen
import com.popchat.ui.theme.PopChatTheme
import com.popchat.ui.theme.PopChatYellow

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    onLoginClick: () -> Unit
) {
    var currentPage by remember { mutableStateOf(0) }
    val pages = remember { onboardingPages }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Page content
        androidx.compose.foundation.pager.HorizontalPager(
            count = pages.size,
            state = androidx.compose.foundation.pager.rememberPagerState(initialPage = currentPage),
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            onPageChanged = { currentPage = it }
        ) { page ->
            val data = pages[page]
            OnboardingPage(
                illustration = data.illustration,
                title = data.title,
                subtitle = data.subtitle
            )
        }

        // Bottom section with pagination and buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Pagination dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pages.indices.forEach { index ->
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .size(if (index == currentPage) 24.dp else 8.dp, 8.dp)
                            .background(
                                color = if (index == currentPage) PopChatBlue else PopChatBlue.copy(alpha = 0.3f),
                                shape = androidx.compose.ui.graphics.RoundedCornerShape(4.dp)
                            )
                            .animateContentSize()
                    )
                }
            }

            // Action buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentPage == pages.lastIndex) {
                    PillButton(
                        text = "Get Started",
                        onClick = onFinish,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    PillButton(
                        text = "Get Started",
                        onClick = { /* Navigate to next or finish */ },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                TextButton(
                    text = "Already have an account? Log In",
                    onClick = onLoginClick
                )
            }
        }
    }
}

@Composable
fun OnboardingPage(
    illustration: (@Composable () -> Unit)? = null,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Skip button at top right
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopEnd
        ) {
            TextButton(
                text = "Skip",
                onClick = { /* Skip onboarding */ },
                modifier = Modifier.padding(24.dp),
                color = PopChatTheme.colorScheme.onSurfaceVariant
            )
        }

        // Illustration
        illustration?.invoke() ?: DefaultIllustration()

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 48.dp))

        // Title
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = PopChatTheme.colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

        // Subtitle
        Text(
            text = subtitle,
            fontSize = 16.sp,
            color = PopChatTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
            lineHeight = 24.sp
        )
    }
}

@Composable
fun DefaultIllustration() {
    // Floating speech bubbles animation
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "")
    
    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // Background bubble
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(PopChatBlue.copy(alpha = 0.1f), androidx.compose.ui.graphics.CircleShape)
        )
        
        // Main "pop" bubble
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(PopChatBlue, androidx.compose.ui.graphics.CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "pop",
                fontSize = 36.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White
            )
        }
        
        // Accent bubbles
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(PopChatGreen, androidx.compose.ui.graphics.CircleShape)
                .offset(x = 80.dp, y = -40.dp)
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(PopChatYellow, androidx.compose.ui.graphics.CircleShape)
                .offset(x = -70.dp, y = 50.dp)
        )
        Box(
            modifier = Modifier
                .size(35.dp)
                .background(PopChatBlue.copy(alpha = 0.7f), androidx.compose.ui.graphics.CircleShape)
                .offset(x = 60.dp, y = 70.dp)
        )
    }
}

data class OnboardingPageData(
    val illustration: (@Composable () -> Unit)?,
    val title: String,
    val subtitle: String
)

val onboardingPages = listOf(
    OnboardingPageData(
        illustration = { DefaultIllustration() },
        title = "Chat Without Limits",
        subtitle = "Send texts, voice notes, photos, videos, and files instantly. No limits, no compression."
    ),
    OnboardingPageData(
        illustration = { PeopleIllustration() },
        title = "Meet New People",
        subtitle = "Discover communities, join group chats, and connect with people who share your interests."
    ),
    OnboardingPageData(
        illustration = { SecureIllustration() },
        title = "Stay Secure & Private",
        subtitle = "End-to-end encryption, disappearing messages, and full control over your data."
    )
)

@Composable
fun PeopleIllustration() {
    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // Stacked profile circles
        (0..3).forEach { index ->
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        color = when (index % 3) {
                            0 -> PopChatBlue
                            1 -> PopChatGreen
                            else -> PopChatYellow
                        },
                        shape = androidx.compose.ui.graphics.CircleShape
                    )
                    .border(3.dp, androidx.compose.ui.graphics.Color.White, androidx.compose.ui.graphics.CircleShape)
                    .offset(x = (-30 * index).dp, y = (15 * index).dp)
                    .zIndex((3 - index).toFloat())
            ) {
                // Could add initials here
            }
        }
    }
}

@Composable
fun SecureIllustration() {
    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // Shield icon
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(PopChatBlue.copy(alpha = 0.1f), androidx.compose.ui.graphics.CircleShape)
        )
        
        androidx.compose.material3.Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.Shield,
            contentDescription = "Security",
            modifier = Modifier.size(80.dp),
            tint = PopChatBlue
        )
        
        // Check mark
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(PopChatGreen, androidx.compose.ui.graphics.CircleShape)
                .offset(x = 60.dp, y = 60.dp)
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Check,
                contentDescription = "Verified",
                modifier = Modifier.size(24.dp),
                tint = androidx.compose.ui.graphics.Color.White
            )
        }
    }
}
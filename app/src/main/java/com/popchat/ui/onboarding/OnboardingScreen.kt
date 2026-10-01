package com.popchat.ui.onboarding

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.popchat.ui.common.PillButton
import com.popchat.ui.common.TextButton
import com.popchat.ui.theme.GlassLevel
import com.popchat.ui.theme.OmiChatBlue
import com.popchat.ui.theme.OmiChatGreen
import com.popchat.ui.theme.OmiChatYellow
import com.popchat.ui.theme.glassPanel
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    onLoginClick: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) { page ->
            val data = onboardingPages[page]
            OnboardingPage(
                illustration = data.illustration,
                title = data.title,
                subtitle = data.subtitle,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // The active dot shares the glass language so the indicator reads as
            // part of the surface rather than a separate widget.
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(onboardingPages.size) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (selected) 26.dp else 8.dp)
                            .glassPanel(
                                shape = RoundedCornerShape(4.dp),
                                level = GlassLevel.Thin,
                                accent = if (selected) OmiChatBlue else Color.Unspecified,
                            )
                            .background(
                                color = if (selected) {
                                    OmiChatBlue.copy(alpha = 0.85f)
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                                },
                                shape = RoundedCornerShape(4.dp),
                            )
                    )
                }
            }

            val isLastPage = pagerState.currentPage == onboardingPages.lastIndex

            PillButton(
                text = if (isLastPage) "Get Started" else "Next",
                onClick = {
                    if (isLastPage) {
                        onFinish()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )

            TextButton(
                text = "Already have an account? Log In",
                onClick = onLoginClick,
            )
        }
    }
}

@Composable
fun OnboardingPage(
    illustration: (@Composable () -> Unit)? = null,
    title: String,
    subtitle: String,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        illustration?.invoke() ?: DefaultIllustration()

        Spacer(modifier = Modifier.height(40.dp))

        // Copy sits on its own glass card so it stays legible over the animated
        // mesh backdrop, which is busy enough to hurt raw text contrast.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .glassPanel(
                    shape = RoundedCornerShape(28.dp),
                    level = GlassLevel.Thick,
                )
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = subtitle,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * Logo lockup.
 *
 * Three glass orbs drift out of phase on a single shared transition, so the
 * motion stays on one clock instead of three.
 */
@Composable
fun DefaultIllustration() {
    val transition = rememberInfiniteTransition(label = "onboarding")
    val breathe by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathe",
    )
    val pulse by transition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, delayMillis = 400),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )

    Box(
        modifier = Modifier.size(260.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Soft halo behind the mark.
        Box(
            modifier = Modifier
                .size(220.dp)
                .scale(breathe)
                .alpha(pulse * 0.5f)
                .background(
                    color = OmiChatBlue.copy(alpha = 0.10f),
                    shape = CircleShape,
                )
        )

        Box(
            modifier = Modifier
                .size(150.dp)
                .scale(breathe)
                .glassPanel(
                    shape = CircleShape,
                    level = GlassLevel.Ultra,
                    accent = OmiChatBlue,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "omi",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }

        OrbDot(
            color = OmiChatGreen,
            size = 52,
            offsetX = 86,
            offsetY = (-46),
            delayMillis = 0,
        )
        OrbDot(
            color = OmiChatYellow,
            size = 42,
            offsetX = (-76),
            offsetY = 54,
            delayMillis = 260,
        )
        OrbDot(
            color = OmiChatBlue,
            size = 36,
            offsetX = 64,
            offsetY = 76,
            delayMillis = 520,
        )
    }
}

/** A small drifting glass orb used to decorate the illustrations. */
@Composable
private fun OrbDot(
    color: Color,
    size: Int,
    offsetX: Int,
    offsetY: Int,
    delayMillis: Int,
) {
    val transition = rememberInfiniteTransition(label = "orb$size")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, delayMillis = delayMillis),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "drift$size",
    )

    Box(
        modifier = Modifier
            .size(size.dp)
            .offset(x = (offsetX + drift).dp, y = (offsetY - drift).dp)
            .glassPanel(
                shape = CircleShape,
                level = GlassLevel.Thick,
                accent = color,
            ),
    )
}

data class OnboardingPageData(
    val illustration: (@Composable () -> Unit)?,
    val title: String,
    val subtitle: String,
)

val onboardingPages = listOf(
    OnboardingPageData(
        illustration = { DefaultIllustration() },
        title = "Chat Without Limits",
        subtitle = "Send texts, voice notes, photos, videos, and files instantly. No limits, no compression.",
    ),
    OnboardingPageData(
        illustration = { PeopleIllustration() },
        title = "Meet New People",
        subtitle = "Discover communities, join group chats, and connect with people who share your interests.",
    ),
    OnboardingPageData(
        illustration = { SecureIllustration() },
        title = "Stay Secure & Private",
        subtitle = "End-to-end encryption, disappearing messages, and full control over your data.",
    ),
)

@Composable
fun PeopleIllustration() {
    Box(
        modifier = Modifier.size(260.dp),
        contentAlignment = Alignment.Center,
    ) {
        val palette = listOf(OmiChatBlue, OmiChatGreen, OmiChatYellow)
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .offset(x = (-30 * index).dp, y = (15 * index).dp)
                    .zIndex((3 - index).toFloat())
                    .glassPanel(
                        shape = CircleShape,
                        level = GlassLevel.Thick,
                        accent = palette[index % palette.size],
                    )
                    .border(
                        width = 2.dp,
                        color = Color.White.copy(alpha = 0.45f),
                        shape = CircleShape,
                    )
            )
        }
    }
}

@Composable
fun SecureIllustration() {
    Box(
        modifier = Modifier.size(260.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(170.dp)
                .glassPanel(
                    shape = CircleShape,
                    level = GlassLevel.Thick,
                    accent = OmiChatBlue,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Security",
                modifier = Modifier.size(84.dp),
                tint = OmiChatBlue,
            )
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .offset(x = 62.dp, y = 62.dp)
                .glassPanel(
                    shape = CircleShape,
                    level = GlassLevel.Thick,
                    accent = OmiChatGreen,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Verified",
                modifier = Modifier.size(24.dp),
                tint = Color.White,
            )
        }
    }
}
package com.popchat.ui.theme

import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random

/* ------------------------------------------------------------------------- */
/*                                  Tokens                                    */
/* ------------------------------------------------------------------------- */

/** True when the device can run a real `RenderEffect` blur (Android 12+). */
val canBlurBackdrop: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/**
 * Thickness of a frosted panel. Heavier levels tint more, gain a stronger
 * specular rim and sit higher, so a [GlassLevel.Thin] chip resting on a
 * [GlassLevel.Thick] sheet still reads as "behind glass".
 */
enum class GlassLevel(
    val tintAlpha: Float,
    val borderAlpha: Float,
    val sheenAlpha: Float,
    val elevation: Dp,
) {
    /** Chips, small pills, inline toolbars. */
    Thin(tintAlpha = 0.32f, borderAlpha = 0.20f, sheenAlpha = 0.55f, elevation = 4.dp),

    /** Cards, list rows, message bubbles. The default. */
    Regular(tintAlpha = 0.44f, borderAlpha = 0.26f, sheenAlpha = 0.68f, elevation = 10.dp),

    /** Sheets, bars and panels floating over busy content. */
    Thick(tintAlpha = 0.56f, borderAlpha = 0.32f, sheenAlpha = 0.78f, elevation = 18.dp),

    /** Modal surfaces, full-width command panels. */
    Ultra(tintAlpha = 0.68f, borderAlpha = 0.38f, sheenAlpha = 0.86f, elevation = 28.dp),
}

/**
 * The colour half of the glass system, provided by [OmiChatTheme] and read via
 * [OmiChatGlass.tokens]. Override with [OmiChatGlass.LocalTokens] to re-tint a
 * subtree.
 */
@Immutable
data class GlassTokens(
    /** Base body colour of the frosted pane. */
    val tint: Color,
    /** Colour of the specular rim tracing the panel outline. */
    val rim: Color,
    /** Light pooling along the top edge. */
    val sheenTop: Color,
    /** Light pooling along the bottom edge. */
    val sheenBottom: Color,
    /** Opacity of the procedural film grain laid over the pane. */
    val grainAlpha: Float,
    /** Opacity of the drop shadow under the pane. */
    val shadowAlpha: Float,
    /** True when these tokens were built for a dark colour scheme. */
    val isDark: Boolean,
) {
    /**
     * Glass is a strong light diffuser. When the backdrop cannot actually be
     * blurred (Android < 12) the tint is raised so panes stay legible instead of
     * dissolving into the content behind them.
     */
    val tintAlphaScale: Float
        get() = if (canBlurBackdrop) 1f else 1.35f
}

internal val LightGlassTokens = GlassTokens(
    tint = Color(0xFFFFFFFF),
    rim = Color(0xFFFFFFFF),
    sheenTop = Color(0xFFFFFFFF),
    sheenBottom = Color(0xFFB9D3F4),
    grainAlpha = 0.10f,
    shadowAlpha = 0.16f,
    isDark = false,
)

internal val DarkGlassTokens = GlassTokens(
    tint = Color(0xFF1B2430),
    rim = Color(0xFF9FC6F2),
    sheenTop = Color(0xFFBBD9FF),
    sheenBottom = Color(0xFF2F6DB5),
    grainAlpha = 0.07f,
    shadowAlpha = 0.44f,
    isDark = true,
)

private val LocalGlassTokens = staticCompositionLocalOf { LightGlassTokens }

/** Access point for the ambient glass tokens. */
object OmiChatGlass {

    val tokens: GlassTokens
        @Composable @ReadOnlyComposable get() = LocalGlassTokens.current

    /** Re-tints glass for the current subtree. */
    @Composable
    fun LocalTokens(tokens: GlassTokens, content: @Composable () -> Unit) {
        CompositionLocalProvider(LocalGlassTokens provides tokens, content = content)
    }
}

/* ------------------------------------------------------------------------- */
/*                              Surface primitive                             */
/* ------------------------------------------------------------------------- */

private const val GRAIN_SEED = 0x0B1CE
private const val GRAIN_DENSITY = 1400f
private const val GRAIN_MIN = 40
private const val GRAIN_MAX = 260

/**
 * Turns any layout node into a frosted pane: translucent tint, directional
 * sheen, specular rim and film grain.
 *
 * The backdrop blur itself lives in [frosted]. Frosting the backdrop once and
 * then drawing un-blurred panes over it costs a single render effect instead of
 * one per panel.
 *
 * @param shape outline of the pane; also used to clip and to trace the rim.
 * @param level how heavy the glass reads.
 * @param accent overrides [GlassTokens.tint] for brand-coloured panes.
 * @param grain adds procedural film grain, which stops large panes from looking
 *   like flat translucent plastic.
 */
@Composable
fun Modifier.glassPanel(
    shape: Shape = MaterialTheme.shapes.medium,
    level: GlassLevel = GlassLevel.Regular,
    accent: Color = Color.Unspecified,
    grain: Boolean = true,
): Modifier {
    val tokens = LocalGlassTokens.current
    val baseTint = if (accent.isSpecified) accent else tokens.tint
    val tintAlpha = (level.tintAlpha * tokens.tintAlphaScale).coerceIn(0f, 0.92f)
    val rimAlpha = level.borderAlpha
    val sheenAlpha = level.sheenAlpha
    val grainAlpha = tokens.grainAlpha
    val shadowAlpha = tokens.shadowAlpha
    val elevation = level.elevation

    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = shadowAlpha),
            spotColor = Color.Black.copy(alpha = shadowAlpha),
        )
        .clip(shape)
        .drawWithCache {
            val pane = baseTint.copy(alpha = tintAlpha)

            val sheen = Brush.verticalGradient(
                0f to tokens.sheenTop.copy(alpha = sheenAlpha),
                0.35f to Color.Transparent,
                0.75f to Color.Transparent,
                1f to tokens.sheenBottom.copy(alpha = sheenAlpha * 0.55f),
                startY = 0f,
                endY = size.height,
            )

            val rimBrush = Brush.linearGradient(
                colors = listOf(
                    tokens.rim.copy(alpha = rimAlpha * 1.6f),
                    tokens.rim.copy(alpha = rimAlpha * 0.25f),
                    tokens.rim.copy(alpha = rimAlpha),
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height),
            )

            val outline = Path().apply {
                addOutline(shape.createOutline(size, layoutDirection, this@drawWithCache))
            }

            onDrawBehind {
                // 1. Translucent body.
                drawRect(pane)

                // 2. Directional sheen, so the pane appears to have a light source.
                drawRect(sheen)

                // 3. Deterministic film grain: a fixed seed keeps the noise
                //    stable across recompositions, so nothing shimmers.
                if (grain) {
                    val random = Random(GRAIN_SEED)
                    val dots = (size.width * size.height / GRAIN_DENSITY)
                        .toInt()
                        .coerceIn(GRAIN_MIN, GRAIN_MAX)
                    repeat(dots) {
                        drawCircle(
                            color = Color.White.copy(alpha = random.nextFloat() * grainAlpha),
                            radius = 0.3f + random.nextFloat() * 0.8f,
                            center = Offset(
                                x = random.nextFloat() * size.width,
                                y = random.nextFloat() * size.height,
                            ),
                        )
                    }
                }

                // 4. Specular rim traced along the outline.
                drawPath(
                    path = outline,
                    brush = rimBrush,
                    style = Stroke(width = 1.dp.toPx()),
                )
            }
        }
        .border(
            border = BorderStroke(1.dp, tokens.rim.copy(alpha = rimAlpha * 0.6f)),
            shape = shape,
        )
}

/* ------------------------------------------------------------------------- */
/*                                  Backdrop                                  */
/* ------------------------------------------------------------------------- */

/** Brand-tinted blob colours the mesh gradient is built from. */
val GlassPalette: List<Color> = listOf(
    Color(0xFF1E88E5), // OmiChatBlue
    Color(0xFF7C4DFF), // electric violet
    Color(0xFF00BCD4), // cyan
    Color(0xFFFF6B9D), // pink
    Color(0xFFFFC107), // OmiChatYellow
)

private const val TWO_PI = (2.0 * PI).toFloat()

/**
 * A single colour blob inside the mesh gradient.
 *
 * [x]/[y] are the anchor as a fraction of the canvas, [radius] is a fraction of
 * the longest edge, and [sway]/[phase] drive the slow drift so the refraction
 * never looks frozen.
 */
private data class Blob(
    val x: Float,
    val y: Float,
    val radius: Float,
    val sway: Float,
    val phase: Float,
)

private val MeshBlobs = listOf(
    Blob(x = 0.18f, y = 0.14f, radius = 0.85f, sway = 0.55f, phase = 0.00f),
    Blob(x = 0.86f, y = 0.20f, radius = 0.70f, sway = 0.62f, phase = 1.35f),
    Blob(x = 0.50f, y = 0.58f, radius = 1.05f, sway = 0.35f, phase = 0.30f),
    Blob(x = 0.10f, y = 0.88f, radius = 0.78f, sway = 0.70f, phase = 1.05f),
    Blob(x = 0.92f, y = 0.92f, radius = 0.90f, sway = 0.48f, phase = 1.62f),
)

/**
 * The colour field that gives the glass something to refract.
 *
 * Without a busy, moving backdrop frosting degenerates into plain translucency
 * — this is the part that actually sells the effect.
 *
 * @param animated slowly drifts the blobs so refraction reads as liquid.
 */
@Composable
fun GlassBackdrop(
    modifier: Modifier = Modifier,
    animated: Boolean = true,
    palette: List<Color> = GlassPalette,
) {
    val tokens = LocalGlassTokens.current
    val base = if (tokens.isDark) Color(0xFF070B12) else Color(0xFFEFF5FD)
    val strength = if (tokens.isDark) 0.55f else 0.42f
    val colors = remember(palette) { palette.map { it.copy(alpha = strength) } }

    val drift = if (animated && !LocalInspectionMode.current) {
        rememberInfiniteTransition(label = "glassBackdrop").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 22_000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "glassDrift",
        )
    } else {
        remember { mutableStateOf(0.5f) }
    }

    val phase = drift.value

    Box(
        modifier = modifier.drawWithCache {
            val span = max(size.width, size.height)

            onDrawBehind {
                drawRect(base)

                MeshBlobs.forEachIndexed { index, blob ->
                    val color = colors[index % colors.size]
                    val angle = (blob.phase + phase) * TWO_PI
                    val center = Offset(
                        x = (blob.x + blob.sway * 0.10f * cos(angle)) * size.width,
                        y = (blob.y + blob.sway * 0.08f * sin(angle)) * size.height,
                    )
                    val radius = span * blob.radius

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                color,
                                color.copy(alpha = strength * 0.35f),
                                Color.Transparent,
                            ),
                            center = center,
                            radius = radius,
                        ),
                        radius = radius,
                        center = center,
                    )
                }
            }
        }
    )
}

/**
 * Applies a real Gaussian blur to this layer's rendered content.
 *
 * `Modifier.blur` only blurs the node it is attached to, never what is drawn
 * behind it, so the correct usage is to frost the *backdrop* once and lay
 * un-blurred panes over it:
 *
 * ```
 * Box {
 *     GlassBackdrop(Modifier.fillMaxSize().frosted(24.dp))
 *     // ... glass panels on top ...
 * }
 * ```
 *
 * Below Android 12 there is no `RenderEffect`, so this degrades to a no-op and
 * the panes lean on a heavier tint instead (see [GlassTokens.tintAlphaScale]).
 */
fun Modifier.frosted(radius: Dp, shape: Shape = RectangleShape): Modifier {
    val clipped = this.clip(shape)
    if (radius <= 0.dp) return clipped
    // Blur needs the platform RenderEffect, which arrived in API 31. The
    // comparison is written out literally rather than reusing
    // [canBlurBackdrop] because lint can only follow a direct SDK_INT check.
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return clipped
    return clipped.graphicsLayer {
        // radius.toPx() resolves against GraphicsLayerScope, which is a Density.
        renderEffect = android.graphics.RenderEffect.createBlurEffect(
            // Positional, not named: the Android SDK stubs name these
            // parameters p0/p1/p2, so named arguments do not resolve.
            radius.toPx(),
            radius.toPx(),
            android.graphics.Shader.TileMode.CLAMP,
        ).asComposeRenderEffect()
    }
}

/* ------------------------------------------------------------------------- */
/*                                 Components                                 */
/* ------------------------------------------------------------------------- */

/** A frosted card — the default container for grouped content. */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    level: GlassLevel = GlassLevel.Regular,
    accent: Color = Color.Unspecified,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .glassPanel(shape = shape, level = level, accent = accent)
            .padding(contentPadding),
        content = content,
    )
}

/**
 * A frosted navigation or app bar. Meant to sit at a screen edge where content
 * scrolls underneath it.
 */
@Composable
fun GlassBar(
    modifier: Modifier = Modifier,
    level: GlassLevel = GlassLevel.Thick,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .glassPanel(shape = RoundedCornerShape(0.dp), level = level)
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

/** A circular frosted control, used for call buttons and quick actions. */
@Composable
fun GlassOrb(
    modifier: Modifier = Modifier,
    level: GlassLevel = GlassLevel.Thick,
    accent: Color = Color.Unspecified,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.glassPanel(
            shape = CircleShape,
            level = level,
            accent = accent,
        ),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/**
 * Screen container: a frosted mesh backdrop plus arbitrary content.
 *
 * Content is drawn un-blurred above the backdrop, so every `glassPanel()`
 * inside it correctly refracts the mesh instead of smearing itself.
 */
@Composable
fun GlassScaffold(
    modifier: Modifier = Modifier,
    blur: Dp = 28.dp,
    animated: Boolean = true,
    palette: List<Color> = GlassPalette,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        GlassBackdrop(
            modifier = Modifier
                .fillMaxSize()
                .frosted(blur),
            animated = animated,
            palette = palette,
        )
        Box(modifier = Modifier.fillMaxSize(), content = content)
    }
}

/**
 * Applies glass only when [enabled].
 *
 * Lets a screen opt out wholesale — a Settings toggle, or an accessibility
 * "reduce transparency" preference — without every call site growing an `if`.
 */
@Composable
fun Modifier.glassIfEnabled(
    enabled: Boolean,
    shape: Shape,
    level: GlassLevel = GlassLevel.Regular,
): Modifier = if (enabled) glassPanel(shape = shape, level = level) else this
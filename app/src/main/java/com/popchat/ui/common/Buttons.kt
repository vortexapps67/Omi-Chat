package com.popchat.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton as MaterialTextButton
import androidx.compose.material3.TextButtonDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.ui.theme.GlassLevel
import com.popchat.ui.theme.OmiChatBlue
import com.popchat.ui.theme.glassPanel

enum class IconPosition { Start, End }

/**
 * Primary call to action.
 *
 * Stays a solid brand gradient rather than glass: glass on a button reads as
 * "dismiss", and this is the one control on each screen that must read as
 * "commit".
 */
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    iconPosition: IconPosition = IconPosition.End,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(28.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 8.dp,
            pressedElevation = 2.dp,
            disabledElevation = 0.dp,
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) OmiChatBlue else OmiChatBlue.copy(alpha = 0.5f),
            contentColor = Color.White,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            when {
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp,
                )

                else -> {
                    if (icon != null && iconPosition == IconPosition.Start) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.White,
                        )
                    }
                    Text(
                        text = text,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                    )
                    if (icon != null && iconPosition == IconPosition.End) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.White,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Secondary action.
 *
 * Uses a glass pane with a tinted rim rather than a flat outline, so it sits
 * legibly on the mesh backdrop without competing with [PillButton].
 */
@Composable
fun OutlinedPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    val accent = OmiChatBlue
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(28.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = accent,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .glassPanel(
                    shape = RoundedCornerShape(28.dp),
                    level = GlassLevel.Thin,
                    accent = accent.copy(alpha = 0.16f),
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = accent,
                )
            }
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = accent,
            )
        }
    }
}

/** Ghost / text button. */
@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = OmiChatBlue,
    fontSize: Int = 14,
) {
    MaterialTextButton(
        onClick = onClick,
        modifier = modifier,
        colors = TextButtonDefaults.textButtonColors(contentColor = color),
    ) {
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Medium,
            color = color,
        )
    }
}

/**
 * Filter chip.
 *
 * Selected chips get a solid brand fill so the active filter is unambiguous;
 * unselected chips are frosted, which keeps the row visually quiet.
 */
@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .then(
                if (isSelected) {
                    Modifier
                        .glassPanel(shape = shape, level = GlassLevel.Thin, accent = OmiChatBlue)
                        .border(1.dp, OmiChatBlue.copy(alpha = 0.6f), shape)
                } else {
                    Modifier.glassPanel(shape = shape, level = GlassLevel.Thin)
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else colors.onSurfaceVariant,
        )
    }
}

/** Circular icon button on a frosted disc. */
@Composable
fun CircleIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 40,
    backgroundColor: Color = OmiChatBlue.copy(alpha = 0.1f),
    iconColor: Color = OmiChatBlue,
    enabled: Boolean = true,
    contentDescription: String? = null,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(size.dp),
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.5f),
            contentColor = if (enabled) iconColor else iconColor.copy(alpha = 0.5f),
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size((size * 0.6f).toInt().dp),
        )
    }
}

/** App-bar back affordance. */
@Composable
fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    CircleIconButton(
        icon = Icons.Default.ArrowBack,
        onClick = onClick,
        modifier = modifier,
        size = 40,
        contentDescription = "Back",
    )
}

/** Text field on a frosted pane. */
@Composable
fun OutlinedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    visualTransformation: VisualTransformation? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1,
) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(16.dp)
    val interactionSource = remember { MutableInteractionSource() }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .glassPanel(shape = shape, level = GlassLevel.Thin)
            .padding(horizontal = 4.dp),
        label = if (label.isNotEmpty()) {
            {
                Text(
                    text = label,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
        } else {
            null
        },
        placeholder = {
            Text(
                text = placeholder,
                color = colors.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(start = 16.dp),
            )
        },
        singleLine = singleLine,
        maxLines = maxLines,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        interactionSource = interactionSource,
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            errorBorderColor = Color.Transparent,
            focusedTextColor = colors.onSurface,
            unfocusedTextColor = colors.onSurface,
            focusedLabelColor = OmiChatBlue,
            unfocusedLabelColor = colors.onSurfaceVariant,
            focusedPlaceholderColor = colors.onSurfaceVariant.copy(alpha = 0.6f),
            unfocusedPlaceholderColor = colors.onSurfaceVariant.copy(alpha = 0.6f),
            cursorColor = OmiChatBlue,
            focusedLeadingIconColor = OmiChatBlue,
            unfocusedLeadingIconColor = colors.onSurfaceVariant,
            focusedTrailingIconColor = OmiChatBlue,
            unfocusedTrailingIconColor = colors.onSurfaceVariant,
        ),
        trailingIcon = if (trailingIcon != null) {
            {
                IconButton(
                    onClick = { onTrailingIconClick?.invoke() },
                    modifier = Modifier.padding(end = 4.dp),
                ) {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant,
                    )
                }
            }
        } else {
            null
        },
    )
}

/**
 * Composer bar.
 *
 * A floating frosted bar rather than an opaque strip: the message list scrolls
 * underneath it and stays visible through the glass, which is what makes the
 * effect read as depth rather than as a border.
 */
@Composable
fun MessageInputBar(
    messageText: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttach: () -> Unit,
    onMic: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .glassPanel(
                shape = RoundedCornerShape(28.dp),
                level = GlassLevel.Thick,
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CircleIconButton(
            icon = Icons.Default.Add,
            onClick = onAttach,
            size = 44,
            backgroundColor = Color.Transparent,
            iconColor = colors.onSurfaceVariant,
            contentDescription = "Attach",
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .glassPanel(
                    shape = RoundedCornerShape(24.dp),
                    level = GlassLevel.Thin,
                )
                .padding(horizontal = 16.dp),
        ) {
            TextField(
                value = messageText,
                onValueChange = onTextChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Message",
                        color = colors.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                },
                singleLine = true,
                textStyle = TextStyle(
                    color = colors.onSurface,
                    fontSize = 16.sp,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedTextColor = colors.onSurface,
                    unfocusedTextColor = colors.onSurface,
                    cursorColor = OmiChatBlue,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
            )
        }

        CircleIconButton(
            icon = if (messageText.isNotBlank()) Icons.Default.Send else Icons.Default.Mic,
            onClick = if (messageText.isNotBlank()) onSend else onMic,
            size = 48,
            backgroundColor = OmiChatBlue,
            iconColor = Color.White,
            contentDescription = if (messageText.isNotBlank()) "Send" else "Record voice message",
        )
    }
}
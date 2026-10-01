package com.popchat.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.ui.theme.PopChatBlue
import com.popchat.ui.theme.PopChatBlueContainer
import com.popchat.ui.theme.PopChatGreen
import com.popchat.ui.theme.PopChatTheme

// Primary Pill Button - Main CTA
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconPosition: IconPosition = IconPosition.End
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) PopChatBlue else PopChatBlue.copy(alpha = 0.5f),
            contentColor = androidx.compose.ui.graphics.Color.White
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = androidx.compose.ui.graphics.Color.White,
                    strokeWidth = 3.dp
                )
            } else {
                if (icon != null && iconPosition == IconPosition.Start) {
                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp).padding(end = 8.dp))
                }
                Text(
                    text = text,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = androidx.compose.ui.graphics.Color.White
                )
                if (icon != null && iconPosition == IconPosition.End) {
                    Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp).padding(start = 8.dp))
                }
            }
        }
    }
}

enum class IconPosition { Start, End }

// Secondary Outlined Pill Button
@Composable
fun OutlinedPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            contentColor = PopChatBlue,
            outlineColor = PopChatBlue
        ),
        border = androidx.compose.ui.graphics.Outline.Border(2.dp, PopChatBlue)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon?.let {
                Icon(imageVector = it, contentDescription = null, modifier = Modifier.size(20.dp).padding(end = 8.dp))
            }
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = PopChatBlue
            )
        }
    }
}

// Ghost/Text Button
@Composable
fun TextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = PopChatBlue,
    fontSize: Int = 14
) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        modifier = modifier,
        colors = androidx.compose.material3.TextButtonDefaults.textButtonColors(
            contentColor = color
        )
    ) {
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

// Filter Chip / Segmented Control
@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = PopChatTheme.colorScheme
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(
                color = if (isSelected) PopChatBlue else colors.surfaceContainerHighest,
                shape = RoundedCornerShape(20.dp)
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

// Icon Button with circular background
@Composable
fun CircleIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 40,
    backgroundColor: Color = PopChatBlue.copy(alpha = 0.1f),
    iconColor: Color = PopChatBlue,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(size.dp),
        enabled = enabled,
        colors = androidx.compose.material3.IconButtonDefaults.iconButtonColors(
            containerColor = if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.5f),
            contentColor = if (enabled) iconColor else iconColor.copy(alpha = 0.5f)
        )
    ) {
        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
    }
}

// Back Button for App Bar
@Composable
fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    CircleIconButton(
        icon = Icons.Default.ArrowBack,
        onClick = onClick,
        modifier = modifier,
        size = 40
    )
}

// Input Field with label and optional trailing icon
@Composable
fun OutlinedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation? = null,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    val colors = PopChatTheme.colorScheme
    val interactionSource = androidx.compose.foundation.interaction.MutableInteractionSource()
    val isFocused = androidx.compose.foundation.focus.rememberFocusRequester()

    androidx.compose.material3.OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label, color = colors.onSurfaceVariant) },
        placeholder = { Text(text = placeholder, color = colors.onSurfaceVariant.copy(alpha = 0.6f)) },
        singleLine = singleLine,
        maxLines = maxLines,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
            containerColor = colors.surface,
            focusedContainerColor = colors.surface,
            disabledContainerColor = colors.surfaceContainerHighest,
            unfocusedContainerColor = colors.surface,
            labelColor = colors.onSurfaceVariant,
            focusedLabelColor = PopChatBlue,
            placeholderColor = colors.onSurfaceVariant.copy(alpha = 0.6f),
            textColor = colors.onSurface,
            cursorColor = PopChatBlue,
            focusedBorderColor = PopChatBlue,
            unfocusedBorderColor = if (isError) colors.error else colors.outlineVariant,
            disabledBorderColor = colors.outlineVariant,
            errorColor = colors.error
        ),
        shape = RoundedCornerShape(12.dp),
        trailingIcon = trailingIcon?.let {
            {
                IconButton(
                    onClick = onTrailingIconClick!!,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(imageVector = it, contentDescription = null, tint = colors.onSurfaceVariant)
                }
            }
        }
    )
}

// Message Input Bar
@Composable
fun MessageInputBar(
    messageText: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttach: () -> Unit,
    onMic: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = PopChatTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .background(colors.surfaceContainer),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        CircleIconButton(
            icon = Icons.Default.Add,
            onClick = onAttach,
            size = 44,
            backgroundColor = colors.surfaceContainerHighest,
            iconColor = colors.onSurfaceVariant
        )

        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(colors.surface, RoundedCornerShape(24.dp))
        ) {
            androidx.compose.material3.TextField(
                value = messageText,
                onValueChange = onTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 0.dp),
                placeholder = { Text("Type a message...", color = colors.onSurfaceVariant.copy(alpha = 0.6f)) },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Send),
                colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    disabledContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                    textColor = colors.onSurface,
                    cursorColor = PopChatBlue,
                    placeholderColor = colors.onSurfaceVariant.copy(alpha = 0.6f)
                )
            )
        }

        if (messageText.isNotBlank()) {
            CircleIconButton(
                icon = Icons.Default.Send,
                onClick = onSend,
                size = 48,
                backgroundColor = PopChatBlue,
                iconColor = androidx.compose.ui.graphics.Color.White
            )
        } else {
            CircleIconButton(
                icon = Icons.Default.Mic,
                onClick = onMic,
                size = 48,
                backgroundColor = PopChatBlue,
                iconColor = androidx.compose.ui.graphics.Color.White
            )
        }
    }
}
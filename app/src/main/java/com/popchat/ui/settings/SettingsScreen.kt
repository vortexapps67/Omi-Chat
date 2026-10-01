package com.popchat.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.ui.common.PillButton
import com.popchat.ui.theme.GlassLevel
import com.popchat.ui.theme.glassPanel

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onItemClick: (String) -> Unit,
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var biometricEnabled by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            modifier = Modifier.glassPanel(
                shape = RoundedCornerShape(0.dp),
                level = GlassLevel.Thick,
            ),
            title = { Text("Settings") },
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            // Generous bottom padding keeps the last group clear of the floating
            // navigation bar.
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item {
                SettingsSection(
                    title = "Account",
                    items = listOf(
                        SettingsItem("Profile", Icons.Default.Person) { onItemClick("profile") },
                        SettingsItem("Privacy & Security", Icons.Default.Security) { onItemClick("privacy") },
                        SettingsItem(
                            title = "Notifications",
                            icon = Icons.Default.Notifications,
                            trailing = {
                                Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = { notificationsEnabled = it },
                                )
                            },
                        ) { onItemClick("notifications") },
                    ),
                )
            }

            item {
                SettingsSection(
                    title = "Appearance",
                    items = listOf(
                        SettingsItem(
                            title = "Dark Mode",
                            icon = Icons.Default.DarkMode,
                            trailing = {
                                Switch(
                                    checked = darkModeEnabled,
                                    onCheckedChange = { darkModeEnabled = it },
                                )
                            },
                        ) { onItemClick("darkMode") },
                        SettingsItem("Theme Color", Icons.Default.Palette) { onItemClick("themeColor") },
                        SettingsItem("Font Size", Icons.Default.FormatSize) { onItemClick("fontSize") },
                    ),
                )
            }

            item {
                SettingsSection(
                    title = "Chats",
                    items = listOf(
                        SettingsItem("Chat Backup", Icons.Default.Backup) { onItemClick("backup") },
                        SettingsItem("Disappearing Messages", Icons.Default.Timer) { onItemClick("disappearing") },
                        SettingsItem("Media Auto-Download", Icons.Default.Download) { onItemClick("mediaDownload") },
                    ),
                )
            }

            item {
                SettingsSection(
                    title = "Security",
                    items = listOf(
                        SettingsItem(
                            title = "Biometric Lock",
                            icon = Icons.Default.Fingerprint,
                            trailing = {
                                Switch(
                                    checked = biometricEnabled,
                                    onCheckedChange = { biometricEnabled = it },
                                )
                            },
                        ) { onItemClick("biometric") },
                        SettingsItem("Two-Factor Authentication", Icons.Default.VerifiedUser) { onItemClick("2fa") },
                        SettingsItem("Active Sessions", Icons.Default.Devices) { onItemClick("sessions") },
                        SettingsItem("Blocked Contacts", Icons.Default.Block) { onItemClick("blocked") },
                    ),
                )
            }

            item {
                SettingsSection(
                    title = "Data & Storage",
                    items = listOf(
                        SettingsItem("Storage Usage", Icons.Default.Storage) { onItemClick("storage") },
                        SettingsItem("Data Usage", Icons.Default.DataUsage) { onItemClick("dataUsage") },
                        SettingsItem("Clear Cache", Icons.Default.DeleteOutline) { onItemClick("clearCache") },
                    ),
                )
            }

            item {
                SettingsSection(
                    title = "About",
                    items = listOf(
                        SettingsItem("Help & Support", Icons.Default.Help) { onItemClick("help") },
                        SettingsItem(
                            title = "Version 1.0.0 (Build 1)",
                            icon = Icons.Default.Info,
                            clickable = false,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        ) { },
                    ),
                )
            }

            item {
                PillButton(
                    text = "Log Out",
                    onClick = { onItemClick("logout") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(56.dp),
                )
            }
        }
    }
}

/**
 * An iOS-style grouped settings block: a frosted card holding labelled rows.
 *
 * Rows share one pane instead of drawing dividers, so the group reads as a
 * single object floating over the mesh rather than a ruled table.
 */
@Composable
fun SettingsSection(
    title: String,
    items: List<SettingsItem>,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 2.dp),
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .glassPanel(
                    shape = RoundedCornerShape(20.dp),
                    level = GlassLevel.Regular,
                )
                .padding(vertical = 4.dp),
        ) {
            items.forEachIndexed { index, item ->
                val rowColor = item.tint ?: colors.onSurface

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = item.clickable, onClick = item.onClick)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        color = rowColor,
                        modifier = Modifier.weight(1f),
                    )
                    item.trailing?.invoke()
                        ?: if (item.clickable) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = colors.onSurfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier.size(20.dp),
                            )
                        } else {
                            null
                        }
                }

                if (index != items.lastIndex) {
                    // Hairline separator, inset to the text baseline. A plain
                    // translucent line reads better here than a glass pane —
                    // frosting a 1dp strip just makes it look thicker.
                    Spacer(
                        modifier = Modifier
                            .padding(start = 54.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(colors.outlineVariant.copy(alpha = 0.6f)),
                    )
                }
            }
        }
    }
}

data class SettingsItem(
    val title: String,
    val icon: ImageVector,
    val trailing: (@Composable () -> Unit)? = null,
    val clickable: Boolean = true,
    val tint: Color? = null,
    val onClick: () -> Unit,
)
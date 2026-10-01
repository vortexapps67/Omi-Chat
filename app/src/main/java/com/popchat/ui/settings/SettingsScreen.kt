package com.popchat.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.ui.common.PillButton
import com.popchat.ui.theme.OmiChatBlue
import com.popchat.ui.theme.OmiChatTheme

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onItemClick: (String) -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var biometricEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        TopAppBar(
            title = { Text("Settings") },
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
            )
        )

        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Account Section
            item {
                SettingsSection(
                    title = "Account",
                    items = listOf(
                        SettingsItem("Profile", androidx.compose.material.icons.Icons.Default.Person) { onItemClick("profile") },
                        SettingsItem("Privacy & Security", androidx.compose.material.icons.Icons.Default.Security) { onItemClick("privacy") },
                        SettingsItem("Notifications", androidx.compose.material.icons.Icons.Default.Notifications, trailing = {
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }) { onItemClick("notifications") },
                    )
                )
            }

            // Appearance Section
            item {
                SettingsSection(
                    title = "Appearance",
                    items = listOf(
                        SettingsItem("Dark Mode", androidx.compose.material.icons.Icons.Default.DarkMode, trailing = {
                            Switch(
                                checked = darkModeEnabled,
                                onCheckedChange = { darkModeEnabled = it },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }) { onItemClick("darkMode") },
                        SettingsItem("Theme Color", androidx.compose.material.icons.Icons.Default.Palette) { onItemClick("themeColor") },
                        SettingsItem("Chat Wallpaper", androidx.compose.material.icons.Icons.Default.Wallpaper) { onItemClick("wallpaper") },
                        SettingsItem("Font Size", androidx.compose.material.icons.Icons.Default.FormatSize) { onItemClick("fontSize") },
                    )
                )
            }

            // Chat Settings Section
            item {
                SettingsSection(
                    title = "Chats",
                    items = listOf(
                        SettingsItem("Chat Backup", androidx.compose.material.icons.Icons.Default.Backup) { onItemClick("backup") },
                        SettingsItem("Disappearing Messages", androidx.compose.material.icons.Icons.Default.Timer) { onItemClick("disappearing") },
                        SettingsItem("Media Auto-Download", androidx.compose.material.icons.Icons.Default.Download) { onItemClick("mediaDownload") },
                        SettingsItem("Chat History", androidx.compose.material.icons.Icons.Default.History) { onItemClick("history") },
                    )
                )
            }

            // Security Section
            item {
                SettingsSection(
                    title = "Security",
                    items = listOf(
                        SettingsItem("Biometric Lock", androidx.compose.material.icons.Icons.Default.Fingerprint, trailing = {
                            Switch(
                                checked = biometricEnabled,
                                onCheckedChange = { biometricEnabled = it },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }) { onItemClick("biometric") },
                        SettingsItem("Two-Factor Authentication", androidx.compose.material.icons.Icons.Default.Shield) { onItemClick("2fa") },
                        SettingsItem("Active Sessions", androidx.compose.material.icons.Icons.Default.Devices) { onItemClick("sessions") },
                        SettingsItem("Blocked Contacts", androidx.compose.material.icons.Icons.Default.Block) { onItemClick("blocked") },
                    )
                )
            }

            // Data & Storage Section
            item {
                SettingsSection(
                    title = "Data & Storage",
                    items = listOf(
                        SettingsItem("Storage Usage", androidx.compose.material.icons.Icons.Default.Storage) { onItemClick("storage") },
                        SettingsItem("Data Usage", androidx.compose.material.icons.Icons.Default.DataUsage) { onItemClick("dataUsage") },
                        SettingsItem("Export Data", androidx.compose.material.icons.Icons.Default.FileDownload) { onItemClick("export") },
                        SettingsItem("Clear Cache", androidx.compose.material.icons.Icons.Default.DeleteSweep) { onItemClick("clearCache") },
                    )
                )
            }

            // About Section
            item {
                SettingsSection(
                    title = "About",
                    items = listOf(
                        SettingsItem("Help & Support", androidx.compose.material.icons.Icons.Default.Help) { onItemClick("help") },
                        SettingsItem("Terms of Service", androidx.compose.material.icons.Icons.Default.Description) { onItemClick("terms") },
                        SettingsItem("Privacy Policy", androidx.compose.material.icons.Icons.Default.PrivacyTip) { onItemClick("privacy") },
                        SettingsItem("Version 1.0.0 (Build 1)", androidx.compose.material.icons.Icons.Default.Info, clickable = false) { },
                    )
                )
            }

            // Logout
            item {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    PillButton(
                        text = "Log Out",
                        onClick = { onItemClick("logout") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    items: List<SettingsItem>
) {
    val colors = OmiChatTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = colors.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )

        items.forEachIndexed { index, item ->
            val isLast = index == items.lastIndex
            ListItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(if (item.clickable) colors.surface else colors.surfaceContainerHighest)
                    .clickable(enabled = item.clickable, onClick = item.onClick),
                leading = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = if (item.clickable) colors.onSurfaceVariant else colors.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                headlineContent = {
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        color = if (item.clickable) colors.onSurface else colors.onSurfaceVariant
                    )
                },
                trailing = item.trailing ?? {
                    if (item.clickable) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            )

            if (!isLast) {
                androidx.compose.material3.Divider(
                    modifier = Modifier.padding(start = 72.dp),
                    color = colors.outlineVariant,
                    thickness = 0.5.dp
                )
            }
        }
    }
}

data class SettingsItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val trailing: (@Composable () -> Unit)? = null,
    val clickable: Boolean = true,
    val onClick: () -> Unit
)
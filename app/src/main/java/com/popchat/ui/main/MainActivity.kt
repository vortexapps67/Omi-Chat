package com.popchat.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.hilt.navigation.compose.hiltNavController
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.bottomNavView
import androidx.navigation.compose.currentBackStackEntryAsState
import dagger.hilt.android.AndroidEntryPoint
import com.popchat.ui.auth.AuthCallbackScreen
import com.popchat.ui.auth.AuthScreen
import com.popchat.ui.chat.ChatScreen
import com.popchat.ui.chatlist.ChatListScreen
import com.popchat.ui.calls.CallHistoryScreen
import com.popchat.ui.discover.DiscoverScreen
import com.popchat.ui.media.SharedMediaScreen
import com.popchat.ui.onboarding.OnboardingScreen
import com.popchat.ui.profile.ProfileScreen
import com.popchat.ui.settings.SettingsScreen
import com.popchat.ui.theme.Theme.OmiChatTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OmiChatTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = currentRoute != "auth" && 
                    currentRoute != "onboarding" && 
                    currentRoute != "auth/callback" &&
                    !currentRoute?.startsWith("chat/") == true &&
                    !currentRoute?.startsWith("shared_media/") == true &&
                    currentRoute != "call_history" &&
                    currentRoute != "settings" &&
                    currentRoute != "profile"

                NavHost(navController, startDestination = "onboarding") {
                    composable("onboarding") {
                        OnboardingScreen(
                            onFinish = { navController.navigate("auth") { popUpTo("onboarding") { inclusive = true } } },
                            onLoginClick = { navController.navigate("auth") }
                        )
                    }
                    composable("auth") {
                        AuthScreen(
                            onLoginClick = { email, password -> 
                                // ViewModel handles auth
                                navController.navigate("main") { popUpTo("auth") { inclusive = true } }
                            },
                            onForgotPassword = { /* Navigate to forgot password */ },
                            onGoogleSignIn = { /* Handle Google sign in */ },
                            onSignUpClick = { navController.navigate("register") }
                        )
                    }
                    composable("register") {
                        AuthScreen.RegisterScreen(
                            onRegisterClick = { name, email, password ->
                                // ViewModel handles registration
                                navController.navigate("main") { popUpTo("auth") { inclusive = true } }
                            },
                            onGoogleSignIn = { /* Handle Google sign in */ },
                            onLoginClick = { navController.popBackStack() }
                        )
                    }
                    composable("auth/callback") {
                        AuthCallbackScreen()
                    }

                    // Main navigation with bottom bar
                    navigation(startDestination = "chats", route = "main") {
                        composable("chats") {
                            ChatListScreen(
                                onOpenChat = { chatId ->
                                    navController.navigate("chat/$chatId")
                                },
                                onNewChat = { navController.navigate("new_chat") },
                                onSearch = { /* Show search */ },
                                onProfileClick = { navController.navigate("profile") }
                            )
                        }
                        composable("calls") {
                            CallHistoryScreen(
                                onBack = { /* Handled by nav */ },
                                onCallBack = { name -> /* Start call */ }
                            )
                        }
                        composable("discover") {
                            DiscoverScreen(onBack = { /* Handled by nav */ })
                        }
                        composable("profile") {
                            ProfileScreen(
                                onBack = { /* Handled by nav */ },
                                onEditProfile = { /* Navigate to edit profile */ },
                                onSettingsClick = { action ->
                                    when (action) {
                                        "logout" -> navController.navigate("auth") { popUpTo("main") { inclusive = true } }
                                        else -> navController.navigate("settings")
                                    }
                                }
                            )
                        }
                    }

                    // Chat detail
                    composable(
                        route = "chat/{chatId}",
                        arguments = listOf(androidx.navigation.navArgument("chatId") { type = androidx.navigation.NavType.StringType })
                    ) {
                        val chatId = it.getString("chatId") ?: ""
                        ChatScreen(
                            chatName = "Chat $chatId",
                            chatAvatar = null,
                            isGroup = false,
                            isOnline = true,
                            onBack = { navController.popBackStack() },
                            onCall = { /* Start voice call */ },
                            onVideoCall = { /* Start video call */ },
                            onMoreOptions = { /* Show options */ }
                        )
                    }

                    // Shared Media
                    composable(
                        route = "shared_media/{chatName}",
                        arguments = listOf(androidx.navigation.navArgument("chatName") { type = androidx.navigation.NavType.StringType })
                    ) {
                        val chatName = it.getString("chatName") ?: ""
                        SharedMediaScreen(
                            chatName = chatName,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // Settings
                    composable("settings") {
                        SettingsScreen(
                            onBack = { navController.popBackStack() },
                            onItemClick = { action ->
                                when (action) {
                                    "logout" -> navController.navigate("auth") { popUpTo("main") { inclusive = true } }
                                    else -> { /* Handle settings navigation */ }
                                }
                            }
                        )
                    }
                }

                // Bottom Navigation Bar
                if (showBottomBar) {
                    BottomNavigationBar(navController = navController)
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: androidx.navigation.NavController) {
    val items = listOf(
        BottomNavItem("Chats", "chats", androidx.compose.material.icons.Icons.Default.ChatBubbleOutline, androidx.compose.material.icons.Icons.Default.ChatBubble),
        BottomNavItem("Calls", "calls", androidx.compose.material.icons.Icons.Default.CallOutline, androidx.compose.material.icons.Icons.Default.Call),
        BottomNavItem("Discover", "discover", androidx.compose.material.icons.Icons.Default.ExploreOutline, androidx.compose.material.icons.Icons.Default.Explore),
        BottomNavItem("Profile", "profile", androidx.compose.material.icons.Icons.Default.PersonOutline, androidx.compose.material.icons.Icons.Default.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.hierarchy?.any { it.route == "main" } == true
    val currentDestination = navBackStackEntry?.destination?.route

    androidx.compose.material3.BottomNavigation(
        modifier = androidx.compose.foundation.layout.Modifier.fillMaxWidth(),
        backgroundColor = OmiChatTheme.colorScheme.surfaceContainerLow,
        containerColor = OmiChatTheme.colorScheme.surfaceContainerLow,
        contentColor = OmiChatTheme.colorScheme.onSurface
    ) {
        items.forEach { item ->
            val isSelected = currentDestination?.startsWith(item.route) == true
            androidx.compose.material3.BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) OmiChatTheme.colorScheme.primary else OmiChatTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = { Text(text = item.label, fontSize = 12.sp) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                alwaysShowLabel = true
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector
)
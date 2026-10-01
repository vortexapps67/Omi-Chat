package com.popchat.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.popchat.ui.auth.AuthCallbackScreen
import com.popchat.ui.auth.LoginScreen
import com.popchat.ui.auth.RegisterScreen
import com.popchat.ui.calls.CallHistoryScreen
import com.popchat.ui.chat.ChatScreen
import com.popchat.ui.chatlist.ChatListScreen
import com.popchat.ui.discover.DiscoverScreen
import com.popchat.ui.media.SharedMediaScreen
import com.popchat.ui.onboarding.OnboardingScreen
import com.popchat.ui.profile.ProfileScreen
import com.popchat.ui.settings.SettingsScreen
import com.popchat.ui.theme.GlassLevel
import com.popchat.ui.theme.OmiChatTheme
import com.popchat.ui.theme.glassPanel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OmiChatTheme {
                OmiChatApp()
            }
        }
    }
}

private data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
)

private val bottomNavItems = listOf(
    BottomNavItem("Chats", "chats", Icons.Filled.ChatBubbleOutline, Icons.Filled.ChatBubbleOutline),
    BottomNavItem("Calls", "calls", Icons.Filled.Call, Icons.Filled.Call),
    BottomNavItem("Discover", "discover", Icons.Filled.Explore, Icons.Filled.Explore),
    BottomNavItem("Profile", "profile", Icons.Filled.Person, Icons.Filled.Person),
)

/** Routes that own the full screen and should not carry the bottom bar. */
private val fullScreenRoutes = setOf(
    "onboarding",
    "auth",
    "register",
    "auth/callback",
    "settings",
)

private fun shouldShowBottomBar(route: String?): Boolean {
    if (route == null) return false
    if (route in fullScreenRoutes) return false
    // Detail routes ("chat/{chatId}", "shared_media/{chatName}") push their own
    // header and are far too tall for a bar plus their own app bar.
    return !route.startsWith("chat/") && !route.startsWith("shared_media/")
}

@Composable
fun OmiChatApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = shouldShowBottomBar(currentRoute)

    Scaffold(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        bottomBar = {
            if (showBottomBar) {
                GlassBottomNavigation(navController)
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "onboarding",
            modifier = Modifier.padding(innerPadding),
        ) {
            composable("onboarding") {
                OnboardingScreen(
                    onFinish = {
                        navController.navigate("auth") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    },
                    onLoginClick = { navController.navigate("auth") },
                )
            }

            composable("auth") {
                LoginScreen(
                    onLoginClick = { _, _ -> },
                    onForgotPassword = { /* Route to the reset-password flow */ },
                    onGoogleSignIn = {
                        navController.navigate("main") {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                    onSignUpClick = { navController.navigate("register") },
                    onSuccess = {
                        navController.navigate("main") {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() },
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegisterClick = { _, _, _ -> },
                    onGoogleSignIn = {
                        navController.navigate("main") {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                    onLoginClick = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate("main") {
                            popUpTo("auth") { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() },
                )
            }

            composable("auth/callback") {
                AuthCallbackScreen()
            }

            navigation(startDestination = "chats", route = "main") {
                composable("chats") {
                    ChatListScreen(
                        onOpenChat = { chatId -> navController.navigate("chat/$chatId") },
                        onNewChat = { navController.navigate("discover") },
                        onSearch = { /* Focus the search field */ },
                        onProfileClick = { navController.navigate("profile") },
                    )
                }

                composable("calls") {
                    CallHistoryScreen(
                        onBack = { navController.popBackStack() },
                        onCallBack = { /* Start a call to this contact */ },
                    )
                }

                composable("discover") {
                    DiscoverScreen(onBack = { navController.popBackStack() })
                }

                composable("profile") {
                    ProfileScreen(
                        onBack = { navController.popBackStack() },
                        onEditProfile = { /* Route to the edit-profile screen */ },
                        onSettingsClick = { action ->
                            when (action) {
                                "logout" -> navController.navigate("auth") {
                                    popUpTo("main") { inclusive = true }
                                }
                                else -> navController.navigate("settings")
                            }
                        },
                    )
                }
            }

            composable(
                route = "chat/{chatId}",
                arguments = listOf(navArgument("chatId") { type = NavType.StringType }),
            ) { entry ->
                val chatId = entry.arguments?.getString("chatId").orEmpty()
                ChatScreen(
                    chatName = "Chat $chatId",
                    chatAvatar = null,
                    isGroup = false,
                    isOnline = true,
                    onBack = { navController.popBackStack() },
                    onCall = { /* Start a voice call */ },
                    onVideoCall = { /* Start a video call */ },
                    onMoreOptions = { /* Show the message actions sheet */ },
                )
            }

            composable(
                route = "shared_media/{chatName}",
                arguments = listOf(navArgument("chatName") { type = NavType.StringType }),
            ) { entry ->
                SharedMediaScreen(
                    chatName = entry.arguments?.getString("chatName").orEmpty(),
                    onBack = { navController.popBackStack() },
                )
            }

            composable("settings") {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onItemClick = { action ->
                        when (action) {
                            "logout" -> navController.navigate("auth") {
                                popUpTo("main") { inclusive = true }
                            }
                            else -> { /* Handle the remaining settings destinations */ }
                        }
                    },
                )
            }
        }
    }
}

/**
 * Floating frosted navigation bar.
 *
 * Sits on a glass pane rather than a Material `NavigationBar` container, so the
 * mesh backdrop stays visible behind the tabs. The selected item gets a tinted
 * glass pill instead of a filled indicator, keeping the same depth language as
 * the rest of the app.
 */
@Composable
fun GlassBottomNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .glassPanel(
                shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
                level = GlassLevel.Thick,
            ),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            val tint by animateColorAsState(
                targetValue = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                label = "navItemTint",
            )

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp),
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = tint,
                    selectedTextColor = tint,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )
        }
    }
}
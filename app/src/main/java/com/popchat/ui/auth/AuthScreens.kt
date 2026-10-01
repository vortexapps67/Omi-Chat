package com.popchat.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.popchat.ui.common.BackButton
import com.popchat.ui.common.OutlinedInputField
import com.popchat.ui.common.OutlinedPillButton
import com.popchat.ui.common.PillButton
import com.popchat.ui.common.TextButton
import com.popchat.ui.theme.GlassLevel
import com.popchat.ui.theme.OmiChatBlue
import com.popchat.ui.theme.glassPanel
import kotlinx.coroutines.launch

/**
 * Sign in.
 *
 * The whole form sits on one frosted card. With an animated mesh backdrop behind
 * it, a single pane gives the fields enough local contrast to stay readable
 * while keeping the background visible around the edges.
 */
@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onForgotPassword: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onSignUpClick: () -> Unit,
    onBack: () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val viewModel: AuthViewModel = viewModel()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            BackButton(onClick = onBack)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            AuthLogo()

            AuthCard {
                Text(
                    text = "Welcome Back",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = "Sign in to continue to Omi Chat",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedInputField(
                    value = email,
                    onValueChange = { email = it; error = null },
                    label = "Email",
                    placeholder = "you@example.com",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                )

                OutlinedInputField(
                    value = password,
                    onValueChange = { password = it; error = null },
                    label = "Password",
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    trailingIcon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    onTrailingIconClick = { passwordVisible = !passwordVisible },
                )

                ErrorBanner(error)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        text = "Forgot password?",
                        onClick = onForgotPassword,
                    )
                }

                PillButton(
                    text = "Log In",
                    isLoading = isLoading,
                    onClick = {
                        isLoading = true
                        scope.launch {
                            viewModel.signIn(email.trim(), password)
                                .onFailure { isLoading = false; error = it.message }
                        }
                        // `onLoginClick` lets the host observe the attempt; the
                        // ViewModel owns whether the session actually succeeded.
                        onLoginClick(email.trim(), password)
                    },
                )

                GlassDivider(label = "or")

                OutlinedPillButton(
                    text = "Continue with Google",
                    onClick = onGoogleSignIn,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Don't have an account? ",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(text = "Sign Up", onClick = onSignUpClick)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/** Create account. Shares the glass card layout with [LoginScreen]. */
@Composable
fun RegisterScreen(
    onRegisterClick: (String, String, String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onLoginClick: () -> Unit,
    onBack: () -> Unit = {},
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val viewModel: AuthViewModel = viewModel()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            BackButton(onClick = onBack)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            AuthLogo()

            AuthCard {
                Text(
                    text = "Create Account",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = "Join Omi Chat today",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedInputField(
                    value = fullName,
                    onValueChange = { fullName = it; error = null },
                    label = "Full Name",
                    placeholder = "Your name",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )

                OutlinedInputField(
                    value = email,
                    onValueChange = { email = it; error = null },
                    label = "Email",
                    placeholder = "you@example.com",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                )

                OutlinedInputField(
                    value = password,
                    onValueChange = { password = it; error = null },
                    label = "Password",
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    trailingIcon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    onTrailingIconClick = { passwordVisible = !passwordVisible },
                )

                ErrorBanner(error)

                PillButton(
                    text = "Sign Up",
                    isLoading = isLoading,
                    onClick = {
                        isLoading = true
                        scope.launch {
                            viewModel.signUp(email.trim(), password, username = fullName.trim(), displayName = fullName.trim())
                                .onFailure { isLoading = false; error = it.message }
                        }
                        onRegisterClick(fullName.trim(), email.trim(), password)
                    },
                )

                GlassDivider(label = "or")

                OutlinedPillButton(
                    text = "Continue with Google",
                    onClick = onGoogleSignIn,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Already have an account? ",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(text = "Log In", onClick = onLoginClick)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Router between sign-in and sign-up.
 *
 * Keeping the toggle here means the host graph declares one auth destination
 * instead of two that both render the same card.
 */
@Composable
fun AuthScreen(
    onNavigateToMain: () -> Unit,
    onGoogleSignIn: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    val viewModel: AuthViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is AuthViewModel.AuthUiState.Loading -> CircularProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp),
            color = OmiChatBlue,
        )

        is AuthViewModel.AuthUiState.Authenticated -> {
            // Navigate once the session exists; the ViewModel owns the truth.
            androidx.compose.runtime.LaunchedEffect(state) { onNavigateToMain() }
        }

        else -> LoginScreen(
            onLoginClick = { _, _ -> },
            onForgotPassword = { },
            onGoogleSignIn = onGoogleSignIn,
            onSignUpClick = { },
            onBack = onBack,
        )
    }
}

/** The frosted pane every auth form lives in. */
@Composable
private fun AuthCard(content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassPanel(
                shape = RoundedCornerShape(28.dp),
                level = GlassLevel.Ultra,
            )
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content,
    )
}

/** Wordmark: a frosted orb rather than a flat brand disc. */
@Composable
fun AuthLogo() {
    Box(
        modifier = Modifier
            .size(72.dp)
            .glassPanel(
                shape = CircleShape,
                level = GlassLevel.Ultra,
                accent = OmiChatBlue,
            )
            .background(OmiChatBlue.copy(alpha = 0.22f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "omi",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
    }
}

/** Hairline "or" rule. */
@Composable
private fun GlassDivider(label: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant),
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant),
        )
    }
}

/** Inline validation message on a tinted pane. */
@Composable
private fun ErrorBanner(error: String?) {
    if (error == null) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassPanel(
                shape = RoundedCornerShape(14.dp),
                level = GlassLevel.Thin,
                accent = MaterialTheme.colorScheme.error.copy(alpha = 0.22f),
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = error,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Start,
        )
    }
}

/** Shown while an OAuth redirect is being exchanged for a session. */
@Composable
fun AuthCallbackScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .glassPanel(
                    shape = CircleShape,
                    level = GlassLevel.Ultra,
                    accent = OmiChatBlue,
                ),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(44.dp),
                color = OmiChatBlue,
                strokeWidth = 3.dp,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Completing sign in\u2026",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
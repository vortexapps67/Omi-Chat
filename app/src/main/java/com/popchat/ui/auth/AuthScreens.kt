package com.popchat.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.popchat.ui.common.BackButton
import com.popchat.ui.common.OutlinedInputField
import com.popchat.ui.common.OutlinedPillButton
import com.popchat.ui.common.PillButton
import com.popchat.ui.common.TextButton
import com.popchat.ui.theme.PopChatBlue
import com.popchat.ui.theme.PopChatTheme

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onForgotPassword: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            contentAlignment = Alignment.TopStart
        ) {
            BackButton(onClick = { /* Handle back */ })
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Logo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                LogoMark()
            }

            Text(
                text = "Welcome Back",
                fontSize = 28.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = PopChatTheme.colorScheme.onSurface
            )

            Text(
                text = "Sign in to continue to Pop Chat",
                fontSize = 16.sp,
                color = PopChatTheme.colorScheme.onSurfaceVariant
            )

            // Email/Username field
            OutlinedInputField(
                value = email,
                onValueChange = { email = it },
                label = "Email or Username",
                placeholder = "Enter your email or username",
                keyboardOptions = KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )

            // Password field
            OutlinedInputField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Enter your password",
                visualTransformation = if (isPasswordVisible) androidx.compose.foundation.text.VisualTransformation.None else androidx.compose.foundation.text.PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = true,
                trailingIcon = if (isPasswordVisible) 
                    androidx.compose.material.icons.Icons.Default.VisibilityOff 
                else 
                    androidx.compose.material.icons.Icons.Default.Visibility,
                onTrailingIconClick = { isPasswordVisible = !isPasswordVisible }
            )

            // Forgot password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    text = "Forgot password?",
                    onClick = onForgotPassword,
                    color = PopChatBlue
                )
            }

            // Login button
            PillButton(
                text = "Log In",
                onClick = {
                    isLoading = true
                    onLoginClick(email, password)
                },
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            // Divider with "or"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.weight(1f).height(1.dp).background(PopChatTheme.colorScheme.outlineVariant)
                )
                Text(
                    text = "or",
                    fontSize = 14.sp,
                    color = PopChatTheme.colorScheme.onSurfaceVariant
                )
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.weight(1f).height(1.dp).background(PopChatTheme.colorScheme.outlineVariant)
                )
            }

            // Google Sign In
            OutlinedPillButton(
                text = "Continue with Google",
                onClick = onGoogleSignIn,
                modifier = Modifier.fillMaxWidth(),
                icon = androidx.compose.material.icons.Icons.Default.Google // Would need custom Google icon
            )

            // Sign up link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Don't have an account? ",
                    fontSize = 14.sp,
                    color = PopChatTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    text = "Sign Up",
                    onClick = onSignUpClick,
                    color = PopChatBlue
                )
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterClick: (String, String, String) -> Unit,
    onGoogleSignIn: () -> Unit,
    onLoginClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            contentAlignment = Alignment.TopStart
        ) {
            BackButton(onClick = { /* Handle back */ })
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Logo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                LogoMark()
            }

            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = PopChatTheme.colorScheme.onSurface
            )

            Text(
                text = "Join Pop Chat today",
                fontSize = 16.sp,
                color = PopChatTheme.colorScheme.onSurfaceVariant
            )

            // Full Name / Username
            OutlinedInputField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Full Name",
                placeholder = "Enter your full name",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true
            )

            // Email
            OutlinedInputField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                placeholder = "Enter your email",
                keyboardOptions = KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )

            // Password
            OutlinedInputField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                placeholder = "Create a password",
                visualTransformation = if (isPasswordVisible) androidx.compose.foundation.text.VisualTransformation.None else androidx.compose.foundation.text.PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                singleLine = true,
                trailingIcon = if (isPasswordVisible) 
                    androidx.compose.material.icons.Icons.Default.VisibilityOff 
                else 
                    androidx.compose.material.icons.Icons.Default.Visibility,
                onTrailingIconClick = { isPasswordVisible = !isPasswordVisible }
            )

            // Sign Up button
            PillButton(
                text = "Sign Up",
                onClick = {
                    isLoading = true
                    onRegisterClick(fullName, email, password)
                },
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.weight(1f).height(1.dp).background(PopChatTheme.colorScheme.outlineVariant)
                )
                Text(
                    text = "or",
                    fontSize = 14.sp,
                    color = PopChatTheme.colorScheme.onSurfaceVariant
                )
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.weight(1f).height(1.dp).background(PopChatTheme.colorScheme.outlineVariant)
                )
            }

            // Google Sign In
            OutlinedPillButton(
                text = "Continue with Google",
                onClick = onGoogleSignIn,
                modifier = Modifier.fillMaxWidth()
            )

            // Login link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    fontSize = 14.sp,
                    color = PopChatTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    text = "Log In",
                    onClick = onLoginClick,
                    color = PopChatBlue
                )
            }
        }
    }
}

@Composable
fun LogoMark() {
    Box(
        modifier = Modifier.size(56.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(PopChatBlue, androidx.compose.ui.graphics.CircleShape)
        ) {
            Text(
                text = "pop",
                fontSize = 18.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White
            )
        }
    }
}

@Composable
fun AuthCallbackScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = PopChatBlue,
            strokeWidth = 4.dp
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
        Text(
            text = "Completing sign in...",
            fontSize = 16.sp,
            color = PopChatTheme.colorScheme.onSurfaceVariant
        )
    }
}
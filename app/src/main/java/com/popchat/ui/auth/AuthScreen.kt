package com.popchat.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.popchat.ui.theme.Theme.OmiChatTheme
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onNavigateToMain: () -> Unit
) {
    val viewModel: AuthViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo
            Text(
                text = "OmiChat",
                fontSize = 48.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = androidx.compose.material3.MaterialTheme.colorScheme.primary
            )

            Text(
                text = if (isSignUp) "Create your account" else "Welcome back",
                fontSize = 18.sp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorMessage != null
            )

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = androidx.compose.foundation.text.PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (isSignUp) {
                // Username
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Display Name
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    if (isSignUp) {
                        viewModel.viewModelScope.launch {
                            val result = viewModel.signUp(email, password, username, displayName)
                            isLoading = false
                            result.onSuccess { _ ->
                                onNavigateToMain()
                            }.onFailure { e ->
                                errorMessage = e.message
                            }
                        }
                    } else {
                        viewModel.viewModelScope.launch {
                            val result = viewModel.signIn(email, password)
                            isLoading = false
                            result.onSuccess { _ ->
                                onNavigateToMain()
                            }.onFailure { e ->
                                errorMessage = e.message
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(if (isSignUp) "Sign Up" else "Sign In")
                }
            }

            TextButton(onClick = { isSignUp = !isSignUp }) {
                Text(if (isSignUp) "Already have an account? Sign In" else "Don't have an account? Sign Up")
            }
        }
    }
}
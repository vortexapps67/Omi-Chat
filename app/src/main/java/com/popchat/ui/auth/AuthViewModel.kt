package com.popchat.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.popchat.data.repository.AuthRepository
import com.popchat.data.repository.AuthRepository.AuthState
import com.popchat.data.supabase.model.AuthResponse
import com.popchat.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.observeAuthState()
                .map { state ->
                    when (state) {
                        is AuthState.SignedIn -> AuthUiState.Authenticated(state.user, state.session)
                        is AuthState.SignedOut -> AuthUiState.Unauthenticated(state.error)
                        AuthState.Loading -> AuthUiState.Loading
                    }
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    suspend fun signUp(email: String, password: String, username: String, displayName: String): Result<AuthResponse> {
        _uiState.value = AuthUiState.Loading
        return try {
            val response = authRepository.signUp(email, password, username, displayName)
            if (response.error != null) {
                Result.failure(Exception(response.error!!.message))
            } else {
                Result.success(response)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<AuthResponse> {
        _uiState.value = AuthUiState.Loading
        return try {
            val response = authRepository.signIn(email, password)
            if (response.error != null) {
                Result.failure(Exception(response.error!!.message))
            } else {
                Result.success(response)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        authRepository.signOut()
    }

    sealed interface AuthUiState {
        data object Loading : AuthUiState
        data class Authenticated(val user: com.popchat.data.supabase.model.SupabaseUser, val session: com.popchat.data.supabase.model.SupabaseSession) : AuthUiState
        data class Unauthenticated(val error: String?) : AuthUiState
        data object Idle : AuthUiState
    }
}
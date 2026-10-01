package com.popchat.data.repository

import com.popchat.data.supabase.model.AuthResponse
import com.popchat.data.supabase.model.SupabaseSession
import com.popchat.data.supabase.model.SupabaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val currentUser: Flow<SupabaseUser?>
    val currentSession: Flow<SupabaseSession?>

    suspend fun signUp(email: String, password: String, username: String, displayName: String): AuthResponse

    suspend fun signIn(email: String, password: String): AuthResponse

    suspend fun signInWithOAuth(provider: String): AuthResponse

    suspend fun signOut()

    suspend fun resetPassword(email: String): AuthResponse

    suspend fun updatePassword(newPassword: String): AuthResponse

    suspend fun updateProfile(username: String?, displayName: String?, avatarUrl: String?): AuthResponse

    suspend fun refreshSession(): SupabaseSession?

    suspend fun getCurrentUser(): SupabaseUser?

    fun observeAuthState(): Flow<AuthState>

    sealed interface AuthState {
        data class SignedIn(val user: SupabaseUser, val session: SupabaseSession) : AuthState
        data class SignedOut(val error: String?) : AuthState
        object Loading : AuthState
    }
}
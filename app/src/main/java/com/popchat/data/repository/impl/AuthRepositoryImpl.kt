package com.popchat.data.repository.impl

import com.popchat.data.repository.AuthRepository
import com.popchat.data.supabase.SupabaseClientProvider
import com.popchat.data.supabase.model.AuthResponse
import com.popchat.data.supabase.model.SupabaseSession
import com.popchat.data.supabase.model.SupabaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClientProvider
) : AuthRepository {

    private val _currentUser = MutableStateFlow<SupabaseUser?>(null)
    private val _currentSession = MutableStateFlow<SupabaseSession?>(null)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)

    override val currentUser = _currentUser.asStateFlow()
    override val currentSession = _currentSession.asStateFlow()

    override fun observeAuthState() = _authState.asStateFlow()

    init {
        initializeAuthListener()
    }

    private fun initializeAuthListener() {
        val client = supabaseClient.getClientOrThrow()
        client.auth.onAuthStateChange { event, session ->
            when (event) {
                "SIGNED_IN", "TOKEN_REFRESHED", "USER_UPDATED" -> {
                    session?.let {
                        val supabaseSession = SupabaseSession(
                            accessToken = it.accessToken,
                            refreshToken = it.refreshToken,
                            expiresAt = it.expiresAt,
                            user = it.user
                        )
                        _currentSession.value = supabaseSession
                        _currentUser.value = it.user
                        _authState.value = AuthState.SignedIn(it.user, supabaseSession)
                    }
                }
                "SIGNED_OUT" -> {
                    _currentUser.value = null
                    _currentSession.value = null
                    _authState.value = AuthState.SignedOut(null)
                }
                else -> {}
            }
        }

        // Check for existing session
        client.auth.session?.let { session ->
            _currentSession.value = SupabaseSession(
                accessToken = session.accessToken,
                refreshToken = session.refreshToken,
                expiresAt = session.expiresAt,
                user = session.user
            )
            _currentUser.value = session.user
            _authState.value = AuthState.SignedIn(session.user, _currentSession.value!!)
        } ?: run {
            _authState.value = AuthState.SignedOut(null)
        }
    }

    override suspend fun signUp(email: String, password: String, username: String, displayName: String): AuthResponse {
        val client = supabaseClient.getClientOrThrow()
        val response = client.auth.signUp(
            email = email,
            password = password,
            data = mapOf(
                "username" to username,
                "display_name" to displayName
            )
        )
        return AuthResponse(
            user = response.user,
            session = response.session?.let { session ->
                SupabaseSession(
                    accessToken = session.accessToken,
                    refreshToken = session.refreshToken,
                    expiresAt = session.expiresAt,
                    user = session.user
                )
            },
            error = response.error?.let { AuthResponse.AuthError(it.message, it.code) }
        )
    }

    override suspend fun signIn(email: String, password: String): AuthResponse {
        val client = supabaseClient.getClientOrThrow()
        val response = client.auth.signIn(email = email, password = password)
        return AuthResponse(
            user = response.user,
            session = response.session?.let { session ->
                SupabaseSession(
                    accessToken = session.accessToken,
                    refreshToken = session.refreshToken,
                    expiresAt = session.expiresAt,
                    user = session.user
                )
            },
            error = response.error?.let { AuthResponse.AuthError(it.message, it.code) }
        )
    }

    override suspend fun signInWithOAuth(provider: String): AuthResponse {
        val client = supabaseClient.getClientOrThrow()
        val response = client.auth.signInWithOAuth(provider = provider)
        return AuthResponse(
            user = response.user,
            session = response.session?.let { session ->
                SupabaseSession(
                    accessToken = session.accessToken,
                    refreshToken = session.refreshToken,
                    expiresAt = session.expiresAt,
                    user = session.user
                )
            },
            error = response.error?.let { AuthResponse.AuthError(it.message, it.code) }
        )
    }

    override suspend fun signOut() {
        val client = supabaseClient.getClientOrThrow()
        client.auth.signOut()
        _currentUser.value = null
        _currentSession.value = null
        _authState.value = AuthState.SignedOut(null)
    }

    override suspend fun resetPassword(email: String): AuthResponse {
        val client = supabaseClient.getClientOrThrow()
        val response = client.auth.resetPasswordForEmail(email = email)
        return AuthResponse(
            user = null,
            session = null,
            error = response.error?.let { AuthResponse.AuthError(it.message, it.code) }
        )
    }

    override suspend fun updatePassword(newPassword: String): AuthResponse {
        val client = supabaseClient.getClientOrThrow()
        val response = client.auth.updateUser(password = newPassword)
        return AuthResponse(
            user = response.user,
            session = response.session?.let { session ->
                SupabaseSession(
                    accessToken = session.accessToken,
                    refreshToken = session.refreshToken,
                    expiresAt = session.expiresAt,
                    user = session.user
                )
            },
            error = response.error?.let { AuthResponse.AuthError(it.message, it.code) }
        )
    }

    override suspend fun updateProfile(username: String?, displayName: String?, avatarUrl: String?): AuthResponse {
        val client = supabaseClient.getClientOrThrow()
        val data = mutableMapOf<String, String>()
        username?.let { data["username"] = it }
        displayName?.let { data["display_name"] = it }
        avatarUrl?.let { data["avatar_url"] = it }

        val response = client.auth.updateUser(data = data)
        return AuthResponse(
            user = response.user,
            session = response.session?.let { session ->
                SupabaseSession(
                    accessToken = session.accessToken,
                    refreshToken = session.refreshToken,
                    expiresAt = session.expiresAt,
                    user = session.user
                )
            },
            error = response.error?.let { AuthResponse.AuthError(it.message, it.code) }
        )
    }

    override suspend fun refreshSession(): SupabaseSession? {
        val client = supabaseClient.getClientOrThrow()
        val session = client.auth.refreshSession()
        return session?.let { s ->
            SupabaseSession(
                accessToken = s.accessToken,
                refreshToken = s.refreshToken,
                expiresAt = s.expiresAt,
                user = s.user
            )
        }
    }

    override suspend fun getCurrentUser(): SupabaseUser? {
        return _currentUser.value
    }
}
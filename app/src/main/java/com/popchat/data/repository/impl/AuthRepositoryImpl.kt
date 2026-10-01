package com.popchat.data.repository.impl

import com.popchat.data.repository.AuthRepository
import com.popchat.data.repository.AuthRepository.AuthState
import com.popchat.data.supabase.SupabaseClientProvider
import com.popchat.data.supabase.auth.toAppSession
import com.popchat.data.supabase.auth.toAppUser
import com.popchat.data.supabase.model.AuthError
import com.popchat.data.supabase.model.AuthResponse
import com.popchat.data.supabase.model.SupabaseSession
import com.popchat.data.supabase.model.SupabaseUser
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Supabase-backed authentication.
 *
 * Written against supabase-kt 2.0.0, whose auth plugin exposes a
 * `sessionStatus: StateFlow<SessionStatus>` rather than the callback-based
 * `onAuthStateChange` of the 0.x line this file was originally drafted for.
 * That state flow is the single source of truth for the three public flows
 * here, so a sign-in triggered from any screen updates all of them.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val supabaseClientProvider: SupabaseClientProvider
) : AuthRepository {

    /**
     * Repository lifetime matches process lifetime (it is an app-scoped
     * singleton), so this scope is intentionally never cancelled.
     */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _currentUser = MutableStateFlow<SupabaseUser?>(null)
    private val _currentSession = MutableStateFlow<SupabaseSession?>(null)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)

    override val currentUser: StateFlow<SupabaseUser?> = _currentUser.asStateFlow()
    override val currentSession: StateFlow<SupabaseSession?> = _currentSession.asStateFlow()
    override fun observeAuthState(): StateFlow<AuthState> = _authState.asStateFlow()

    private val client: SupabaseClient? = supabaseClientProvider.getClient()

    init {
        // `return` is not allowed in an init block, so this is an if/else.
        val client = client
        if (client == null) {
            // The build has no Supabase credentials baked in (SUPABASE_URL or
            // SUPABASE_PUBLISHABLE_KEY blank in .env). Report signed out rather
            // than throwing: this runs during singleton construction, so a
            // throw here would take down the launcher Activity.
            _authState.value = AuthState.SignedOut("Supabase is not configured for this build.")
        } else {
            scope.launch {
                client.auth.sessionStatus.collect(::onSessionStatus)
            }
        }
    }

    private fun onSessionStatus(status: SessionStatus) {
        when (status) {
            is SessionStatus.Authenticated -> {
                val session = status.session.toAppSession()
                _currentSession.value = session
                _currentUser.value = session.user
                _authState.value = AuthState.SignedIn(session.user, session)
            }

            SessionStatus.NotAuthenticated -> {
                _currentUser.value = null
                _currentSession.value = null
                _authState.value = AuthState.SignedOut(null)
            }

            // Restoring from disk, or a network failure while doing so. Neither
            // is a decision the UI can act on, so leave the previous state up
            // rather than flashing the signed-out screen.
            SessionStatus.LoadingFromStorage,
            is SessionStatus.NetworkError -> Unit
        }
    }

    /**
     * The auth plugin.
     *
     * `SupabaseClient.auth` is an extension property declared in
     * `io.github.jan.supabase.gotrue`, so the `Auth` import above is what puts
     * the `auth` name in scope here.
     */
    private val auth: Auth
        get() = checkNotNull(client) { "Supabase client is not initialised" }.auth

    override suspend fun signUp(
        email: String,
        password: String,
        username: String,
        displayName: String
    ): AuthResponse = runCatchingAuth {
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("username", username)
                put("display_name", displayName)
            }
        }
        // signUpWith returns the created account record, not a session. When the
        // Supabase project has email confirmation disabled the session is
        // established on the client as a side effect, so read it back from
        // there; otherwise it stays null and the caller must await the
        // confirmation email.
        AuthResponse(
            user = auth.currentUserOrNull()?.toAppUser(),
            session = auth.currentSessionOrNull()?.toAppSession(),
            error = null
        )
    }

    override suspend fun signIn(email: String, password: String): AuthResponse = runCatchingAuth {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        val session = auth.currentSessionOrNull()?.toAppSession()
        AuthResponse(
            user = session?.user ?: auth.currentUserOrNull()?.toAppUser(),
            session = session,
            error = null
        )
    }

    /**
     * Not implemented.
     *
     * supabase-kt's OAuth providers need either a desktop URL opener or, on
     * Android, an Activity to receive the redirect and exchange for a session
     * via [Auth.exchangeCodeForSession]. A repository has no Activity to launch
     * or receive on, so this needs to be wired at the UI layer instead.
     */
    override suspend fun signInWithOAuth(provider: String): AuthResponse = AuthResponse(
        user = null,
        session = null,
        error = AuthError(
            message = "Sign-in with $provider is not wired up yet.",
            code = "oauth_unsupported"
        )
    )

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun resetPassword(email: String): AuthResponse = runCatchingAuth {
        auth.resetPasswordForEmail(email = email)
        AuthResponse(user = null, session = null, error = null)
    }

    override suspend fun updatePassword(newPassword: String): AuthResponse = runCatchingAuth {
        val info = auth.modifyUser { password = newPassword }
        AuthResponse(
            user = info.toAppUser(),
            session = auth.currentSessionOrNull()?.toAppSession(),
            error = null
        )
    }

    override suspend fun updateProfile(
        username: String?,
        displayName: String?,
        avatarUrl: String?
    ): AuthResponse = runCatchingAuth {
        val info = auth.modifyUser {
            data = buildJsonObject {
                username?.let { put("username", it) }
                displayName?.let { put("display_name", it) }
                avatarUrl?.let { put("avatar_url", it) }
            }
        }
        AuthResponse(
            user = info.toAppUser(),
            session = auth.currentSessionOrNull()?.toAppSession(),
            error = null
        )
    }

    override suspend fun refreshSession(): SupabaseSession? = try {
        // refreshSession is keyed on a refresh token, so a stored session has
        // to exist first.
        val current = auth.currentSessionOrNull()
        if (current == null) null else auth.refreshSession(current.refreshToken)?.toAppSession()
    } catch (e: Exception) {
        null
    }

    override suspend fun getCurrentUser(): SupabaseUser? = _currentUser.value

    /**
     * Runs a Supabase call and folds any failure into an [AuthResponse] error
     * rather than letting it propagate.
     *
     * The repository contract returns errors as data, so every screen that
     * calls it can render an inline message without a try/catch.
     */
    private inline fun runCatchingAuth(block: () -> AuthResponse): AuthResponse = try {
        block()
    } catch (e: Exception) {
        AuthResponse(
            user = null,
            session = null,
            error = AuthError(
                message = e.message?.takeIf { it.isNotBlank() } ?: "Something went wrong",
                code = e.javaClass.simpleName
            )
        )
    }
}

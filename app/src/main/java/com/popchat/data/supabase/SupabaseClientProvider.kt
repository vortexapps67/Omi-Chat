package com.popchat.data.supabase

import android.content.Context
import com.popchat.BuildConfig
import com.popchat.util.AppLogger
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth

object SupabaseClientProvider {

    private const val TAG = "SupabaseClientProvider"

    @Volatile
    private var instance: SupabaseClient? = null

    fun initialize(context: Context) {
        if (instance == null) {
            synchronized(this) {
                if (instance == null) {
                    val supabaseUrl = BuildConfig.SUPABASE_URL
                    val supabasePublishableKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY

                    if (supabaseUrl.isBlank() || supabasePublishableKey.isBlank()) {
                        AppLogger.w(TAG, "Supabase credentials not configured. Please set SUPABASE_URL and SUPABASE_PUBLISHABLE_KEY in .env or GitHub secrets")
                        return
                    }

                    // SupabaseClientBuilder's constructor and build() are
                    // internal to the library; createSupabaseClient is the
                    // supported entry point. Per-plugin settings are applied
                    // with install(), and the auth knobs live on AuthConfig.
                    instance = createSupabaseClient(supabaseUrl, supabasePublishableKey) {
                        install(Auth) {
                            // Keep the stored session fresh without the user
                            // having to sign in again on every launch.
                            autoSaveToStorage = true
                            autoLoadFromStorage = true
                            alwaysAutoRefresh = true
                        }
                    }

                    AppLogger.i(TAG, "Supabase client initialized")
                }
            }
        }
    }

    fun getClient(): SupabaseClient? {
        return instance
    }

    fun getClientOrThrow(): SupabaseClient {
        return instance ?: throw IllegalStateException(
            "Supabase client not initialized. Call SupabaseClientProvider.initialize(context) first."
        )
    }

    fun shutdown() {
        instance = null
    }
}
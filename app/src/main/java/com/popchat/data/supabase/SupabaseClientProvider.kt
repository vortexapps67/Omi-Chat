package com.popchat.data.supabase

import android.content.Context
import com.popchat.BuildConfig
import com.popchat.util.AppLogger
import io.github.jan_tennert.supabase_kt.SupabaseClient
import io.github.jan_tennert.supabase_kt.SupabaseClientBuilder

object SupabaseClientProvider {

    private const val TAG = "SupabaseClientProvider"

    @Volatile
    private var instance: SupabaseClient? = null

    fun initialize(context: Context) {
        if (instance == null) {
            synchronized(this) {
                if (instance == null) {
                    val supabaseUrl = BuildConfig.SUPABASE_URL
                    val supabaseAnonKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY

                    if (supabaseUrl.isBlank() || supabaseAnonKey.isBlank()) {
                        AppLogger.w(TAG, "Supabase credentials not configured. Please set SUPABASE_URL and SUPABASE_PUBLISHABLE_KEY in .env or GitHub secrets")
                        return
                    }

                    instance = SupabaseClientBuilder(supabaseUrl, supabaseAnonKey)
                        .apply {
                            // Enable auto-refresh of auth tokens
                            autoRefreshToken = true
                            // Log level for debugging
                            // logLevel = LogLevel.DEBUG
                        }
                        .build()

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
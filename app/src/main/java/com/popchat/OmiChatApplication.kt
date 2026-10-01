package com.popchat

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.popchat.data.supabase.SupabaseClientProvider
import com.popchat.util.AppLogger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class OmiChatApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        try {
            AppLogger.init(this)
        } catch (e: Throwable) {
            // Ignore logger initialization issue
        }
        try {
            SupabaseClientProvider.initialize(this)
        } catch (e: Throwable) {
            AppLogger.e("OmiChatApplication", "Failed to initialize Supabase: ${e.message}", e)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = runCatching {
            if (::hiltWorkerFactory.isInitialized) {
                Configuration.Builder()
                    .setWorkerFactory(hiltWorkerFactory)
                    .build()
            } else {
                Configuration.Builder().build()
            }
        }.getOrElse {
            Configuration.Builder().build()
        }
}
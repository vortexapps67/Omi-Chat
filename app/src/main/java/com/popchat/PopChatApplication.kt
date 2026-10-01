package com.popchat

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.popchat.data.supabase.SupabaseClientProvider
import com.popchat.di.DatabaseModule
import com.popchat.di.NetworkModule
import com.popchat.di.RepositoryModule
import com.popchat.util.AppLogger
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.work.HiltWorkFactory
import javax.inject.Inject

@HiltAndroidApp
class PopChatApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        AppLogger.init(this)
        // Initialize Supabase client early
        SupabaseClientProvider.initialize(this)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()
    }

    companion object {
        @Suppress("UNUSED_PARAMETER")
        fun getEntryPoint(context: Context): ApplicationEntryPoint {
            return EntryPointAccessors.fromApplication(context, ApplicationEntryPoint::class.java)
        }
    }

    @dagger.hilt.EntryPoint
    @dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
    interface ApplicationEntryPoint {
        fun databaseModule(): DatabaseModule
        fun networkModule(): NetworkModule
        fun repositoryModule(): RepositoryModule
    }
}
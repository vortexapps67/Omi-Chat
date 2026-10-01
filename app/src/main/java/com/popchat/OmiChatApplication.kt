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

    /**
     * Injected rather than fetched through an `@EntryPoint`: because this class
     * is `@HiltAndroidApp`, `HiltWorkerFactory` already lives in the
     * `SingletonComponent` and is injectable here. An entry point that asked for
     * `DatabaseModule` / `NetworkModule` / `RepositoryModule` would not compile
     * either - those are `@Module` objects that declare providers, not bindings
     * anything can depend on.
     */
    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        AppLogger.init(this)
        SupabaseClientProvider.initialize(this)
    }

    // Configuration.Provider declares this as a property, not a method: Kotlin
    // sees the Java `getWorkManagerConfiguration()` as the `workManagerConfiguration`
    // property, so overriding it as `fun getWorkManagerConfiguration()` matches
    // nothing and leaves the class abstract.
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()
}
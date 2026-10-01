package com.popchat.di

import android.content.Context
import com.popchat.data.db.AppDatabase
import com.popchat.data.repository.ChatParticipantRepository
import com.popchat.data.repository.ChatRepository
import com.popchat.data.repository.MessageRepository
import com.popchat.data.repository.UserRepository
import com.popchat.data.repository.impl.ChatParticipantRepositoryImpl
import com.popchat.data.repository.impl.ChatRepositoryImpl
import com.popchat.data.repository.impl.MessageRepositoryImpl
import com.popchat.data.repository.impl.UserRepositoryImpl
import com.popchat.data.supabase.SupabaseClientProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(impl: UserRepositoryImpl): UserRepository = impl

    @Provides
    @Singleton
    fun provideChatRepository(impl: ChatRepositoryImpl): ChatRepository = impl

    @Provides
    @Singleton
    fun provideMessageRepository(impl: MessageRepositoryImpl): MessageRepository = impl

    @Provides
    @Singleton
    fun provideChatParticipantRepository(impl: ChatParticipantRepositoryImpl): ChatParticipantRepository = impl
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClientProvider {
        return SupabaseClientProvider
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthRepository(impl: com.popchat.data.repository.impl.AuthRepositoryImpl): com.popchat.data.repository.AuthRepository = impl
}
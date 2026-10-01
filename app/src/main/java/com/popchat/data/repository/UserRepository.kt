package com.popchat.data.repository

import com.popchat.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun getCurrentUser(): UserEntity?

    fun observeCurrentUser(): Flow<UserEntity?>

    suspend fun getUser(userId: String): UserEntity?

    fun observeUser(userId: String): Flow<UserEntity?>

    suspend fun getUsers(userIds: List<String>): List<UserEntity>

    suspend fun searchUsers(query: String): List<UserEntity>

    suspend fun updateUser(user: UserEntity)

    suspend fun syncUserFromSupabase(supabaseUser: com.popchat.data.supabase.model.SupabaseUser, isCurrentUser: Boolean)

    suspend fun setCurrentUser(userId: String)

    suspend fun clearCurrentUser()
}
package com.popchat.data.repository.impl

import com.popchat.data.db.AppDatabase
import com.popchat.data.model.UserEntity
import com.popchat.data.repository.UserRepository
import com.popchat.data.supabase.SupabaseClientProvider
import com.popchat.data.supabase.model.SupabaseUser
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val supabaseClient: SupabaseClientProvider
) : UserRepository {

    override suspend fun getCurrentUser(): UserEntity? {
        return database.userDao().getCurrentUserSync()
    }

    override fun observeCurrentUser() = database.userDao().getCurrentUser()

    override suspend fun getUser(userId: String): UserEntity? {
        return database.userDao().getUserSync(userId)
    }

    override fun observeUser(userId: String) = database.userDao().getUser(userId)

    override suspend fun getUsers(userIds: List<String>): List<UserEntity> {
        return database.userDao().getUsersSync(userIds)
    }

    override suspend fun searchUsers(query: String): List<UserEntity> {
        val searchQuery = "%$query%"
        return database.userDao().searchUsersSync(searchQuery)
    }

    override suspend fun updateUser(user: UserEntity) {
        database.userDao().updateUser(user)
    }

    override suspend fun syncUserFromSupabase(supabaseUser: SupabaseUser, isCurrentUser: Boolean) {
        val entity = UserEntity.fromSupabase(supabaseUser, isCurrentUser)
        database.userDao().insertUser(entity)
    }

    override suspend fun setCurrentUser(userId: String) {
        val current = database.userDao().getCurrentUserSync()
        current?.let {
            database.userDao().updateUser(it.copy(isCurrentUser = false))
        }
        val newUser = database.userDao().getUserSync(userId)
        newUser?.let {
            database.userDao().updateUser(it.copy(isCurrentUser = true))
        }
    }

    override suspend fun clearCurrentUser() {
        val current = database.userDao().getCurrentUserSync()
        current?.let {
            database.userDao().updateUser(it.copy(isCurrentUser = false))
        }
    }
}
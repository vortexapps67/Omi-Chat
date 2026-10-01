package com.popchat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.popchat.data.db.converters.Converters
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "users")
@TypeConverters(Converters::class)
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val username: String,
    val displayName: String?,
    val avatarUrl: String?,
    val status: String? = "online",
    val lastSeen: Instant?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val isCurrentUser: Boolean = false
) {
    companion object {
        fun fromSupabase(user: com.popchat.data.supabase.model.SupabaseUser, isCurrentUser: Boolean = false): UserEntity {
            return UserEntity(
                id = user.id,
                email = user.email ?: "",
                username = user.userMetadata?.getString("username")
                    ?: user.email?.substringBefore("@")
                    ?: "user",
                displayName = user.userMetadata?.getString("display_name"),
                avatarUrl = user.userMetadata?.getString("avatar_url"),
                status = "online",
                lastSeen = null,
                createdAt = Instant.parse(user.createdAt),
                updatedAt = Clock.System.now(),
                isCurrentUser = isCurrentUser
            )
        }
    }
}
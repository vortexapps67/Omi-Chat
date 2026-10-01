package com.popchat.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.popchat.data.db.converters.Converters
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "chats",
    indices = [Index("lastMessageAt"), Index("isGroup")],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["createdBy"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
@TypeConverters(Converters::class)
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val name: String?,
    val avatarUrl: String?,
    val isGroup: Boolean,
    val createdBy: String?,
    val lastMessageId: String?,
    val lastMessagePreview: String?,
    val lastMessageAt: Instant?,
    val unreadCount: Int = 0,
    val isArchived: Boolean = false,
    val isPinned: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        const val TYPE_DIRECT = "direct"
        const val TYPE_GROUP = "group"
        const val TYPE_CHANNEL = "channel"
    }
}
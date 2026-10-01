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
    tableName = "messages",
    indices = [
        Index("chatId"),
        Index("senderId"),
        Index("createdAt"),
        Index(value = ["chatId", "createdAt"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = ChatEntity::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["senderId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["replyToId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
@TypeConverters(Converters::class)
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val chatId: String,
    val senderId: String,
    val content: String,
    val type: String = MessageEntity.TYPE_TEXT,
    val mediaUrl: String?,
    val mediaType: String?,
    val mediaSize: Long?,
    val replyToId: String?,
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deliveredAt: Instant?,
    val readAt: Instant?
) {
    companion object {
        const val TYPE_TEXT = "text"
        const val TYPE_IMAGE = "image"
        const val TYPE_VIDEO = "video"
        const val TYPE_AUDIO = "audio"
        const val TYPE_FILE = "file"
        const val TYPE_LOCATION = "location"
        const val TYPE_CONTACT = "contact"
        const val TYPE_SYSTEM = "system"
    }

    /**
     * Whether this message was sent by the signed-in user.
     *
     * Deliberately not derived here: resolving the current user needs a DI
     * graph, and an entity must stay a pure data holder for Room. Callers that
     * have the session pass their own `isCurrentUser` flag instead.
     */
    fun isOutgoingBy(currentUserId: String?): Boolean =
        currentUserId != null && senderId == currentUserId
}
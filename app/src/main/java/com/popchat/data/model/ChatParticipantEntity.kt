package com.popchat.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(
    tableName = "chat_participants",
    primaryKeys = ["chatId", "userId"],
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
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ChatParticipantEntity(
    val chatId: String,
    val userId: String,
    val role: String = ROLE_MEMBER,
    val joinedAt: Instant = Clock.System.now(),
    // A joining member has read nothing yet and is not muted, so these are
    // nullable but defaulted - otherwise every insert site has to spell out
    // "no read marker, no mute" for it to mean anything.
    val lastReadMessageId: String? = null,
    val isMuted: Boolean = false,
    val mutedUntil: Instant? = null
) {
    companion object {
        const val ROLE_OWNER = "owner"
        const val ROLE_ADMIN = "admin"
        const val ROLE_MEMBER = "member"
    }
}
package com.popchat.ui.chatlist

import androidx.lifecycle.ViewModel
import com.popchat.data.model.ChatEntity
import com.popchat.data.model.ChatParticipantEntity
import com.popchat.data.repository.ChatParticipantRepository
import com.popchat.data.repository.ChatRepository
import com.popchat.data.repository.UserRepository
import com.popchat.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val participantRepository: ChatParticipantRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    /**
     * Every unarchived chat, newest activity first, pinned above the rest.
     *
     * Rows are exposed as entities rather than a UI-ready row type: the list
     * screen needs different projections depending on whether a chat is direct
     * or a group, and it already owns the presentation shape.
     */
    val chats: Flow<List<ChatEntity>> = chatRepository.observeAllChats()

    /** Direct chats only, for the "new message" picker's first tab. */
    val directChats: Flow<List<ChatEntity>> = chatRepository.observeDirectChats()

    val groupChats: Flow<List<ChatEntity>> = chatRepository.observeGroupChats()

    val pinnedChats: Flow<List<ChatEntity>> = chatRepository.observePinnedChats()

    /**
     * Rows for the active search query.
     *
     * The DAO matches on the chat's stored `name`, which is only populated for
     * groups; direct chats get their peer's name filled in by
     * [ensureDirectChatName], so both are searchable once a chat has been
     * opened at least once.
     */
    suspend fun search(query: String): List<ChatEntity> {
        if (query.isBlank()) return emptyList()
        return chatRepository.searchChats(query.trim())
    }

    /**
     * Resolves the chat id to open for [otherUserId], creating it if needed.
     *
     * Reuses an existing 1-on-1 chat so that tapping the same person twice does
     * not fork into two conversations.
     */
    suspend fun createDirectChat(otherUserId: String): Result<String> {
        val currentUserId = userRepository.getCurrentUser()?.id
            ?: return Result.failure(IllegalStateException("Not signed in"))

        if (currentUserId == otherUserId) {
            return Result.failure(IllegalArgumentException("Cannot open a chat with yourself"))
        }

        return try {
            val existing = chatRepository.findDirectChat(currentUserId, otherUserId)
            Result.success(existing?.id ?: createDirectChatWith(currentUserId, otherUserId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun createDirectChatWith(currentUserId: String, otherUserId: String): String {
        val chatId = UUID.randomUUID().toString()
        val now = Clock.System.now()

        // Denormalised on purpose: a direct chat has no name of its own, and
        // every query that orders or searches the list reads this column. If the
        // peer has not been synced yet the row is created with blanks and
        // back-filled on the next sync.
        val peer = userRepository.getUser(otherUserId)

        chatRepository.createChat(
            ChatEntity(
                id = chatId,
                name = peer?.displayName ?: peer?.username,
                avatarUrl = peer?.avatarUrl,
                isGroup = false,
                createdBy = currentUserId,
                lastMessageId = null,
                lastMessagePreview = null,
                lastMessageAt = null,
                unreadCount = 0,
                isArchived = false,
                isPinned = false,
                createdAt = now,
                updatedAt = now
            )
        )

        participantRepository.addParticipants(
            listOf(
                ChatParticipantEntity(
                    chatId = chatId,
                    userId = currentUserId,
                    role = ChatParticipantEntity.ROLE_OWNER
                ),
                ChatParticipantEntity(
                    chatId = chatId,
                    userId = otherUserId,
                    role = ChatParticipantEntity.ROLE_MEMBER
                )
            )
        )

        return chatId
    }

    /**
     * Keeps a direct chat's denormalised name and avatar in step with the peer.
     *
     * Returns the updated chat when a write happened, or null when nothing
     * needed changing - the common case, since this runs on every resume.
     */
    suspend fun ensureDirectChatName(chat: ChatEntity): ChatEntity? {
        if (chat.isGroup) return null

        val currentUserId = userRepository.getCurrentUser()?.id ?: return null
        val peerId = participantRepository.getParticipantIds(chat.id)
            .firstOrNull { it != currentUserId }
            ?: return null

        val peer = userRepository.getUser(peerId) ?: return null
        val name = peer.displayName ?: peer.username

        if (chat.name == name && chat.avatarUrl == peer.avatarUrl) return null

        val updated = chat.copy(name = name, avatarUrl = peer.avatarUrl)
        chatRepository.updateChat(updated)
        return updated
    }

    /** Unread totals across all unarchived chats, for the bottom-nav badge. */
    fun observeTotalUnread(): Flow<Int> = chats.map { list -> list.sumOf { it.unreadCount } }

    suspend fun archiveChat(chatId: String) {
        chatRepository.setArchived(chatId, true)
    }

    suspend fun unarchiveChat(chatId: String) {
        chatRepository.setArchived(chatId, false)
    }

    suspend fun pinChat(chatId: String) {
        chatRepository.setPinned(chatId, true)
    }

    suspend fun unpinChat(chatId: String) {
        chatRepository.setPinned(chatId, false)
    }

    suspend fun deleteChat(chatId: String) {
        chatRepository.deleteChat(chatId)
    }
}
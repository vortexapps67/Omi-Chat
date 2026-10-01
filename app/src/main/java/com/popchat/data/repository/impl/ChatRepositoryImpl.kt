package com.popchat.data.repository.impl

import com.popchat.data.db.AppDatabase
import com.popchat.data.model.ChatEntity
import com.popchat.data.repository.ChatParticipantRepository
import com.popchat.data.repository.ChatRepository
import com.popchat.data.repository.ChatWithParticipants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val participantRepository: ChatParticipantRepository
) : ChatRepository {

    override suspend fun getChat(chatId: String): ChatEntity? {
        return database.chatDao().getChatSync(chatId)
    }

    override fun observeChat(chatId: String) = database.chatDao().getChat(chatId)

    override fun observeAllChats() = database.chatDao().getAllChats()

    override fun observeDirectChats() = database.chatDao().getDirectChats()

    override fun observeGroupChats() = database.chatDao().getGroupChats()

    override fun observePinnedChats() = database.chatDao().getPinnedChats()

    override suspend fun createChat(chat: ChatEntity): ChatEntity {
        database.chatDao().insertChat(chat)
        return chat
    }

    override suspend fun updateChat(chat: ChatEntity) {
        database.chatDao().updateChat(chat)
    }

    override suspend fun updateLastMessage(chatId: String, messageId: String, preview: String, timestamp: Long) {
        database.chatDao().updateLastMessage(chatId, messageId, preview, timestamp)
    }

    override suspend fun incrementUnreadCount(chatId: String) {
        database.chatDao().incrementUnreadCount(chatId)
    }

    override suspend fun clearUnreadCount(chatId: String) {
        database.chatDao().clearUnreadCount(chatId)
    }

    override suspend fun setArchived(chatId: String, archived: Boolean) {
        database.chatDao().setArchived(chatId, archived)
    }

    override suspend fun setPinned(chatId: String, pinned: Boolean) {
        database.chatDao().setPinned(chatId, pinned)
    }

    override suspend fun deleteChat(chatId: String) {
        database.chatDao().deleteChat(chatId)
        participantRepository.removeAllParticipants(chatId)
    }

    override suspend fun searchChats(query: String): List<ChatEntity> {
        return database.chatDao().searchChats("%$query%")
    }

    override suspend fun findDirectChat(firstUserId: String, secondUserId: String): ChatEntity? {
        return database.chatDao().findDirectChat(firstUserId, secondUserId)
    }

    override suspend fun syncChatsFromSupabase() {
        // TODO: Implement Supabase sync
    }

    /**
     * Chat plus its resolved participants.
     *
     * `chat_participants` rows hold ids only, so the profile data has to be
     * joined in from the users table. That join is itself a Flow, so it is
     * flat-mapped: when the roster changes the previous user query is cancelled
     * and a new one starts, which keeps the result live instead of freezing on
     * whatever the first roster happened to be. Participants that have not been
     * synced into the local users table yet simply do not appear.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeChatWithParticipants(chatId: String) = combine(
        database.chatDao().getChat(chatId),
        participantRepository.observeParticipants(chatId).flatMapLatest { participants ->
            val userIds = participants.map { it.userId }
            if (userIds.isEmpty()) {
                flowOf(emptyList())
            } else {
                database.userDao().getUsers(userIds)
            }
        }
    ) { chat, users ->
        ChatWithParticipants(chat!!, users)
    }.distinctUntilChanged()
}
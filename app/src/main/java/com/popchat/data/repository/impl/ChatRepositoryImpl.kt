package com.popchat.data.repository.impl

import com.popchat.data.db.AppDatabase
import com.popchat.data.model.ChatEntity
import com.popchat.data.model.ChatParticipantEntity
import com.popchat.data.model.UserEntity
import com.popchat.data.repository.ChatRepository
import com.popchat.data.repository.ChatWithParticipants
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val participantRepository: com.popchat.data.repository.ChatParticipantRepository
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

    override suspend fun syncChatsFromSupabase() {
        // TODO: Implement Supabase sync
    }

    override fun observeChatWithParticipants(chatId: String) = combine(
        database.chatDao().getChat(chatId),
        participantRepository.observeParticipants(chatId)
    ) { chat, participants ->
        val users = participants.mapNotNull { p ->
            // We'd need to fetch user entities - simplified for now
            null
        }
        ChatWithParticipants(chat!!, users)
    }.distinctUntilChanged()
}
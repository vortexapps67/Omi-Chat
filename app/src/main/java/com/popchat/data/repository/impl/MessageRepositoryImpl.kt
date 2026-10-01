package com.popchat.data.repository.impl

import com.popchat.data.db.AppDatabase
import com.popchat.data.model.MessageEntity
import com.popchat.data.repository.MessageRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : MessageRepository {

    override suspend fun sendMessage(message: MessageEntity): MessageEntity {
        database.messageDao().insertMessage(message)
        return message
    }

    override suspend fun getMessage(messageId: String): MessageEntity? {
        return database.messageDao().getMessageSync(messageId)
    }

    override fun observeMessages(chatId: String, limit: Int, offset: Int) =
        database.messageDao().getMessagesPaged(chatId, limit, offset)

    override suspend fun loadMoreMessages(chatId: String, before: Long, limit: Int): List<MessageEntity> {
        return database.messageDao().getMessagesBefore(chatId, before, limit)
    }

    override suspend fun loadNewMessages(chatId: String, after: Long): List<MessageEntity> {
        return database.messageDao().getMessagesAfter(chatId, after)
    }

    override suspend fun editMessage(messageId: String, content: String) {
        database.messageDao().editMessage(messageId, content, System.currentTimeMillis())
    }

    override suspend fun deleteMessage(messageId: String) {
        database.messageDao().deleteMessage(messageId, System.currentTimeMillis())
    }

    override suspend fun markAsRead(chatId: String, currentUserId: String) {
        database.messageDao().markAsRead(chatId, currentUserId, System.currentTimeMillis())
    }

    override suspend fun markAsDelivered(messageId: String) {
        database.messageDao().markAsDelivered(messageId, System.currentTimeMillis())
    }

    override fun observeUnreadMessages(chatId: String, currentUserId: String) =
        database.messageDao().getUnreadMessages(chatId, currentUserId)

    override suspend fun getUnreadCount(chatId: String, currentUserId: String): Int {
        return database.messageDao().getUnreadMessages(chatId, currentUserId).first().size
    }

    override suspend fun syncMessagesFromSupabase(chatId: String) {
        // TODO: Implement Supabase sync
    }

    override fun observeMediaMessages(chatId: String) =
        database.messageDao().getMediaMessages(chatId)
}
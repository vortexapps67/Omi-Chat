package com.popchat.data.repository

import com.popchat.data.model.MessageEntity
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    suspend fun sendMessage(message: MessageEntity): MessageEntity

    suspend fun getMessage(messageId: String): MessageEntity?

    fun observeMessages(chatId: String, limit: Int, offset: Int): Flow<List<MessageEntity>>

    suspend fun loadMoreMessages(chatId: String, before: Long, limit: Int): List<MessageEntity>

    suspend fun loadNewMessages(chatId: String, after: Long): List<MessageEntity>

    suspend fun editMessage(messageId: String, content: String)

    suspend fun deleteMessage(messageId: String)

    suspend fun markAsRead(chatId: String, currentUserId: String)

    suspend fun markAsDelivered(messageId: String)

    fun observeUnreadMessages(chatId: String, currentUserId: String): Flow<List<MessageEntity>>

    suspend fun getUnreadCount(chatId: String, currentUserId: String): Int

    suspend fun syncMessagesFromSupabase(chatId: String)

    fun observeMediaMessages(chatId: String): Flow<List<MessageEntity>>
}
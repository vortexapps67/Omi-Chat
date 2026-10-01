package com.popchat.data.repository

import com.popchat.data.model.ChatEntity
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun getChat(chatId: String): ChatEntity?

    fun observeChat(chatId: String): Flow<ChatEntity?>

    fun observeAllChats(): Flow<List<ChatEntity>>

    fun observeDirectChats(): Flow<List<ChatEntity>>

    fun observeGroupChats(): Flow<List<ChatEntity>>

    fun observePinnedChats(): Flow<List<ChatEntity>>

    suspend fun createChat(chat: ChatEntity): ChatEntity

    suspend fun updateChat(chat: ChatEntity)

    suspend fun updateLastMessage(chatId: String, messageId: String, preview: String, timestamp: Long)

    suspend fun incrementUnreadCount(chatId: String)

    suspend fun clearUnreadCount(chatId: String)

    suspend fun setArchived(chatId: String, archived: Boolean)

    suspend fun setPinned(chatId: String, pinned: Boolean)

    suspend fun deleteChat(chatId: String)

    suspend fun searchChats(query: String): List<ChatEntity>

    suspend fun syncChatsFromSupabase()

    fun observeChatWithParticipants(chatId: String): Flow<ChatWithParticipants>
}

data class ChatWithParticipants(
    val chat: ChatEntity,
    val participants: List<com.popchat.data.model.UserEntity>
)
package com.popchat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.popchat.data.model.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChats(chats: List<ChatEntity>)

    @Update
    suspend fun updateChat(chat: ChatEntity)

    @Query("SELECT * FROM chats WHERE id = :chatId")
    fun getChat(chatId: String): Flow<ChatEntity?>

    @Query("SELECT * FROM chats WHERE id = :chatId")
    suspend fun getChatSync(chatId: String): ChatEntity?

    @Query("SELECT * FROM chats WHERE isArchived = 0 ORDER BY isPinned DESC, lastMessageAt DESC NULLS LAST")
    fun getAllChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE isArchived = 0 AND isGroup = 0 ORDER BY lastMessageAt DESC NULLS LAST")
    fun getDirectChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE isArchived = 0 AND isGroup = 1 ORDER BY lastMessageAt DESC NULLS LAST")
    fun getGroupChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE isPinned = 1 AND isArchived = 0 ORDER BY lastMessageAt DESC NULLS LAST")
    fun getPinnedChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE id IN (:chatIds)")
    suspend fun getChatsSync(chatIds: List<String>): List<ChatEntity>

    @Query("SELECT * FROM chats WHERE name LIKE :query OR lastMessagePreview LIKE :query LIMIT 20")
    suspend fun searchChats(query: String): List<ChatEntity>

    @Query("UPDATE chats SET unreadCount = unreadCount + 1 WHERE id = :chatId")
    suspend fun incrementUnreadCount(chatId: String)

    @Query("UPDATE chats SET unreadCount = 0 WHERE id = :chatId")
    suspend fun clearUnreadCount(chatId: String)

    @Query("UPDATE chats SET lastMessageId = :messageId, lastMessagePreview = :preview, lastMessageAt = :timestamp WHERE id = :chatId")
    suspend fun updateLastMessage(chatId: String, messageId: String, preview: String, timestamp: Long)

    @Query("UPDATE chats SET isArchived = :archived WHERE id = :chatId")
    suspend fun setArchived(chatId: String, archived: Boolean)

    @Query("UPDATE chats SET isPinned = :pinned WHERE id = :chatId")
    suspend fun setPinned(chatId: String, pinned: Boolean)

    @Query("DELETE FROM chats WHERE id = :chatId")
    suspend fun deleteChat(chatId: String)
}
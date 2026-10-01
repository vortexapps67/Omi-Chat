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

    // Room 2.6's bundled SQLite parser does not accept the SQL NULLS FIRST/LAST
    // modifiers, so descending sorts with nulls last are expressed as a leading
    // `(column IS NULL)` sort key: it yields 0 for present values and 1 for nulls,
    // so ascending puts real timestamps first, then lastMessageAt DESC orders them.

    @Query("SELECT * FROM chats WHERE isArchived = 0 ORDER BY isPinned DESC, (lastMessageAt IS NULL), lastMessageAt DESC")
    fun getAllChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE isArchived = 0 AND isGroup = 0 ORDER BY (lastMessageAt IS NULL), lastMessageAt DESC")
    fun getDirectChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE isArchived = 0 AND isGroup = 1 ORDER BY (lastMessageAt IS NULL), lastMessageAt DESC")
    fun getGroupChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE isPinned = 1 AND isArchived = 0 ORDER BY (lastMessageAt IS NULL), lastMessageAt DESC")
    fun getPinnedChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE id IN (:chatIds)")
    suspend fun getChatsSync(chatIds: List<String>): List<ChatEntity>

    @Query("SELECT * FROM chats WHERE name LIKE :query OR lastMessagePreview LIKE :query LIMIT 20")
    suspend fun searchChats(query: String): List<ChatEntity>

    // A 1-on-1 chat has no name of its own, so the only way to recognise one is
    // by its roster: the two self-joins match a direct chat whose participants
    // are exactly these two users. Passing the same id twice cannot match,
    // which is why callers still guard against self-chat explicitly.
    @Query(
        """
        SELECT c.* FROM chats AS c
        INNER JOIN chat_participants AS a ON a.chatId = c.id AND a.userId = :firstUserId
        INNER JOIN chat_participants AS b ON b.chatId = c.id AND b.userId = :secondUserId
        WHERE c.isGroup = 0
        LIMIT 1
        """
    )
    suspend fun findDirectChat(firstUserId: String, secondUserId: String): ChatEntity?

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
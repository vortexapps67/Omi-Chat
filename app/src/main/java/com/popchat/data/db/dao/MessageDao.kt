package com.popchat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.popchat.data.model.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE id = :messageId")
    fun getMessage(messageId: String): Flow<MessageEntity?>

    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageSync(messageId: String): MessageEntity?

    @Query("SELECT * FROM messages WHERE chatId = :chatId AND isDeleted = 0 ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    fun getMessagesPaged(chatId: String, limit: Int, offset: Int): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE chatId = :chatId AND isDeleted = 0 ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getLatestMessages(chatId: String, limit: Int): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE chatId = :chatId AND isDeleted = 0 AND createdAt < :before ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getMessagesBefore(chatId: String, before: Long, limit: Int): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE chatId = :chatId AND isDeleted = 0 AND createdAt > :after ORDER BY createdAt ASC")
    suspend fun getMessagesAfter(chatId: String, after: Long): List<MessageEntity>

    @Query("SELECT COUNT(*) FROM messages WHERE chatId = :chatId AND isDeleted = 0")
    suspend fun getMessageCount(chatId: String): Int

    @Query("SELECT * FROM messages WHERE chatId = :chatId AND senderId != :currentUserId AND readAt IS NULL AND isDeleted = 0")
    fun getUnreadMessages(chatId: String, currentUserId: String): Flow<List<MessageEntity>>

    @Query("UPDATE messages SET readAt = :readAt WHERE chatId = :chatId AND senderId != :currentUserId AND readAt IS NULL")
    suspend fun markAsRead(chatId: String, currentUserId: String, readAt: Long)

    @Query("UPDATE messages SET deliveredAt = :deliveredAt WHERE id = :messageId")
    suspend fun markAsDelivered(messageId: String, deliveredAt: Long)

    @Query("UPDATE messages SET isEdited = 1, content = :content, updatedAt = :updatedAt WHERE id = :messageId")
    suspend fun editMessage(messageId: String, content: String, updatedAt: Long)

    @Query("UPDATE messages SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :messageId")
    suspend fun deleteMessage(messageId: String, updatedAt: Long)

    @Query("SELECT * FROM messages WHERE replyToId = :messageId")
    suspend fun getReplies(messageId: String): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE chatId = :chatId AND type != 'text' AND isDeleted = 0 ORDER BY createdAt DESC")
    fun getMediaMessages(chatId: String): Flow<List<MessageEntity>>

    @Query("DELETE FROM messages WHERE chatId = :chatId AND createdAt < :cutoff")
    suspend fun cleanupOldMessages(chatId: String, cutoff: Long): Int
}
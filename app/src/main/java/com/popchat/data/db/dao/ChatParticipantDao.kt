package com.popchat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.popchat.data.model.ChatParticipantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatParticipantDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: ChatParticipantEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<ChatParticipantEntity>)

    @Update
    suspend fun updateParticipant(participant: ChatParticipantEntity)

    @Query("SELECT * FROM chat_participants WHERE chatId = :chatId AND userId = :userId")
    fun getParticipant(chatId: String, userId: String): Flow<ChatParticipantEntity?>

    @Query("SELECT * FROM chat_participants WHERE chatId = :chatId")
    fun getParticipants(chatId: String): Flow<List<ChatParticipantEntity>>

    @Query("SELECT * FROM chat_participants WHERE chatId = :chatId")
    suspend fun getParticipantsSync(chatId: String): List<ChatParticipantEntity>

    @Query("SELECT userId FROM chat_participants WHERE chatId = :chatId")
    suspend fun getParticipantIds(chatId: String): List<String>

    @Query("SELECT chatId FROM chat_participants WHERE userId = :userId")
    fun getUserChats(userId: String): Flow<List<String>>

    @Query("SELECT chatId FROM chat_participants WHERE userId = :userId")
    suspend fun getUserChatsSync(userId: String): List<String>

    @Query("UPDATE chat_participants SET lastReadMessageId = :messageId WHERE chatId = :chatId AND userId = :userId")
    suspend fun updateLastRead(chatId: String, userId: String, messageId: String)

    @Query("UPDATE chat_participants SET isMuted = :muted, mutedUntil = :mutedUntil WHERE chatId = :chatId AND userId = :userId")
    suspend fun setMuted(chatId: String, userId: String, muted: Boolean, mutedUntil: Long?)

    @Query("UPDATE chat_participants SET role = :role WHERE chatId = :chatId AND userId = :userId")
    suspend fun updateRole(chatId: String, userId: String, role: String)

    @Query("DELETE FROM chat_participants WHERE chatId = :chatId AND userId = :userId")
    suspend fun removeParticipant(chatId: String, userId: String)

    @Query("DELETE FROM chat_participants WHERE chatId = :chatId")
    suspend fun removeAllParticipants(chatId: String)
}
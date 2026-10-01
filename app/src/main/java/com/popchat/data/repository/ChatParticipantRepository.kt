package com.popchat.data.repository

import com.popchat.data.model.ChatParticipantEntity
import kotlinx.coroutines.flow.Flow

interface ChatParticipantRepository {

    suspend fun addParticipant(participant: ChatParticipantEntity)

    suspend fun addParticipants(participants: List<ChatParticipantEntity>)

    suspend fun getParticipants(chatId: String): List<ChatParticipantEntity>

    fun observeParticipants(chatId: String): Flow<List<ChatParticipantEntity>>

    suspend fun getParticipantIds(chatId: String): List<String>

    suspend fun getUserChats(userId: String): List<String>

    suspend fun updateLastRead(chatId: String, userId: String, messageId: String)

    suspend fun setMuted(chatId: String, userId: String, muted: Boolean, mutedUntil: Long?)

    suspend fun updateRole(chatId: String, userId: String, role: String)

    suspend fun removeParticipant(chatId: String, userId: String)

    suspend fun removeAllParticipants(chatId: String)

    suspend fun isParticipant(chatId: String, userId: String): Boolean

    suspend fun getUserRole(chatId: String, userId: String): String?
}
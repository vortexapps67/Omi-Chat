package com.popchat.data.repository.impl

import com.popchat.data.db.AppDatabase
import com.popchat.data.model.ChatParticipantEntity
import com.popchat.data.repository.ChatParticipantRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatParticipantRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : ChatParticipantRepository {

    override suspend fun addParticipant(participant: ChatParticipantEntity) {
        database.chatParticipantDao().insertParticipant(participant)
    }

    override suspend fun addParticipants(participants: List<ChatParticipantEntity>) {
        database.chatParticipantDao().insertParticipants(participants)
    }

    override suspend fun getParticipants(chatId: String): List<ChatParticipantEntity> {
        return database.chatParticipantDao().getParticipantsSync(chatId)
    }

    override fun observeParticipants(chatId: String) =
        database.chatParticipantDao().getParticipants(chatId)

    override suspend fun getParticipantIds(chatId: String): List<String> {
        return database.chatParticipantDao().getParticipantIds(chatId)
    }

    override suspend fun getUserChats(userId: String): List<String> {
        return database.chatParticipantDao().getUserChatsSync(userId)
    }

    override suspend fun updateLastRead(chatId: String, userId: String, messageId: String) {
        database.chatParticipantDao().updateLastRead(chatId, userId, messageId)
    }

    override suspend fun setMuted(chatId: String, userId: String, muted: Boolean, mutedUntil: Long?) {
        database.chatParticipantDao().setMuted(chatId, userId, muted, mutedUntil)
    }

    override suspend fun updateRole(chatId: String, userId: String, role: String) {
        database.chatParticipantDao().updateRole(chatId, userId, role)
    }

    override suspend fun removeParticipant(chatId: String, userId: String) {
        database.chatParticipantDao().removeParticipant(chatId, userId)
    }

    override suspend fun removeAllParticipants(chatId: String) {
        database.chatParticipantDao().removeAllParticipants(chatId)
    }

    override suspend fun isParticipant(chatId: String, userId: String): Boolean {
        return database.chatParticipantDao().getParticipant(chatId, userId).first() != null
    }

    override suspend fun getUserRole(chatId: String, userId: String): String? {
        return database.chatParticipantDao().getParticipant(chatId, userId).first()?.role
    }
}
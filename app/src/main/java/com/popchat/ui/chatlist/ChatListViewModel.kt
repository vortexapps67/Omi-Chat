package com.popchat.ui.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.popchat.data.model.ChatEntity
import com.popchat.data.repository.ChatRepository
import com.popchat.data.repository.UserRepository
import com.popchat.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _chats = chatRepository.observeAllChats()
        .map { chats ->
            chats.map { chat ->
                ChatListItem(
                    chat = chat,
                    otherUser = if (!chat.isGroup) {
                        // For direct chats, find the other participant
                        null // Would need participant lookup
                    } else null
                )
            }
        }

    val chats = _chats.distinctUntilChanged()

    sealed interface ChatListItem {
        data class ChatItem(
            val chat: ChatEntity,
            val otherUser: com.popchat.data.model.UserEntity?
        ) : ChatListItem
    }

    suspend fun createDirectChat(otherUserId: String): Result<String> {
        return try {
            // Check if direct chat already exists
            val currentUserId = userRepository.getCurrentUser()?.id
            if (currentUserId == null) return Result.failure(Exception("Not logged in"))

            // TODO: Implement chat creation logic
            Result.success("")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun archiveChat(chatId: String) {
        chatRepository.setArchived(chatId, true)
    }

    suspend fun unarchiveChat(chatId: String) {
        chatRepository.setArchived(chatId, false)
    }

    suspend fun pinChat(chatId: String) {
        chatRepository.setPinned(chatId, true)
    }

    suspend fun unpinChat(chatId: String) {
        chatRepository.setPinned(chatId, false)
    }

    suspend fun deleteChat(chatId: String) {
        chatRepository.deleteChat(chatId)
    }
}
package com.popchat.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.popchat.data.model.MessageEntity
import com.popchat.data.model.UserEntity
import com.popchat.data.repository.ChatRepository
import com.popchat.data.repository.MessageRepository
import com.popchat.data.repository.UserRepository
import com.popchat.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages = _messages.asStateFlow().distinctUntilChanged()

    private val _chat = MutableStateFlow<com.popchat.data.model.ChatEntity?>(null)
    val chat = _chat.asStateFlow().distinctUntilChanged()

    private val _otherUser = MutableStateFlow<UserEntity?>(null)
    val otherUser = _otherUser.asStateFlow().distinctUntilChanged()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var currentChatId: String? = null
    private var currentUserId: String? = null
    private val messageChannel = Channel<MessageEntity>(Channel.UNLIMITED)

    init {
        observeMessages()
        observeRealtimeMessages()
    }

    fun initialize(chatId: String) {
        currentChatId = chatId
        currentUserId = userRepository.getCurrentUser()?.id
        loadChat(chatId)
        loadMessages(chatId)
        markAsRead(chatId)
    }

    private fun loadChat(chatId: String) {
        viewModelScope.launch {
            val chatEntity = chatRepository.getChat(chatId)
            _chat.value = chatEntity

            if (!chatEntity?.isGroup == true) {
                // Load other user for direct chat
                val participantIds = chatRepository.observeChatWithParticipants(chatId).first().participants.map { it.id }
                val otherId = participantIds.firstOrNull { it != currentUserId }
                otherId?.let {
                    userRepository.observeUser(it).first().let { _otherUser.value = it }
                }
            }
        }
    }

    private fun loadMessages(chatId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            messageRepository.observeMessages(chatId, 50, 0)
                .collect { msgs ->
                    _messages.value = msgs.reversed()
                    _isLoading.value = false
                }
        }
    }

    private fun observeMessages() {
        // Messages are observed via loadMessages
    }

    private fun observeRealtimeMessages() {
        // TODO: Subscribe to Supabase Realtime for new messages
    }

    suspend fun sendMessage(content: String, type: String = MessageEntity.TYPE_TEXT) {
        val chatId = currentChatId ?: return
        val userId = currentUserId ?: return

        val message = MessageEntity(
            id = java.util.UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = userId,
            content = content,
            type = type,
            mediaUrl = null,
            mediaType = null,
            mediaSize = null,
            replyToId = null,
            isEdited = false,
            isDeleted = false,
            createdAt = kotlinx.datetime.Instant.now(),
            updatedAt = kotlinx.datetime.Instant.now(),
            deliveredAt = null,
            readAt = null
        )

        messageRepository.sendMessage(message)
        chatRepository.updateLastMessage(chatId, message.id, content, message.createdAt.epochMilliseconds)
    }

    suspend fun sendMediaMessage(mediaUrl: String, mediaType: String, mediaSize: Long) {
        val chatId = currentChatId ?: return
        val userId = currentUserId ?: return

        val type = when {
            mediaType.startsWith("image/") -> MessageEntity.TYPE_IMAGE
            mediaType.startsWith("video/") -> MessageEntity.TYPE_VIDEO
            mediaType.startsWith("audio/") -> MessageEntity.TYPE_AUDIO
            else -> MessageEntity.TYPE_FILE
        }

        val message = MessageEntity(
            id = java.util.UUID.randomUUID().toString(),
            chatId = chatId,
            senderId = userId,
            content = "",
            type = type,
            mediaUrl = mediaUrl,
            mediaType = mediaType,
            mediaSize = mediaSize,
            replyToId = null,
            isEdited = false,
            isDeleted = false,
            createdAt = kotlinx.datetime.Instant.now(),
            updatedAt = kotlinx.datetime.Instant.now(),
            deliveredAt = null,
            readAt = null
        )

        messageRepository.sendMessage(message)
        chatRepository.updateLastMessage(chatId, message.id, "[${type}]", message.createdAt.epochMilliseconds)
    }

    suspend fun editMessage(messageId: String, newContent: String) {
        messageRepository.editMessage(messageId, newContent)
    }

    suspend fun deleteMessage(messageId: String) {
        messageRepository.deleteMessage(messageId)
    }

    private suspend fun markAsRead(chatId: String) {
        val userId = currentUserId ?: return
        messageRepository.markAsRead(chatId, userId)
        chatRepository.clearUnreadCount(chatId)
    }

    suspend fun loadMoreMessages() {
        val chatId = currentChatId ?: return
        val oldestMessage = _messages.value.firstOrNull()
        oldestMessage?.let {
            val moreMessages = messageRepository.loadMoreMessages(chatId, it.createdAt.epochMilliseconds, 50)
            _messages.value = moreMessages.reversed() + _messages.value
        }
    }
}
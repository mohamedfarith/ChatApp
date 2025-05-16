package com.learning.chatapp.presentation.chat.viewmodels

import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.learning.chatapp.Constants
import com.learning.chatapp.Resource
import com.learning.chatapp.domain.repository.chat.ChatRepository
import com.learning.chatapp.data.remote.socket.SocketManager
import com.learning.chatapp.data.local.entity.chat.MessageEntity
import com.learning.chatapp.domain.models.MessageStatus
import com.learning.chatapp.domain.usecase.chat.FetchChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCase: FetchChatUseCase,
    private val socketManager: SocketManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val currentUserId = savedStateHandle.get<String>("currentUserId") ?: Constants.CURRENT_USER_ID
    private val otherUserId = savedStateHandle.get<String>("otherUserId") ?: "P1"

    private val _messages = MutableLiveData<Resource<List<MessageEntity>>>()
    val messages: LiveData<Resource<List<MessageEntity>>> = _messages

    init {
        observeSocketEvents()
        socketManager.connect()
        viewModelScope.launch(Dispatchers.IO) {
            socketManager.incomingMessages.collectLatest { message ->
                chatUseCase.insertMessage(message) // Save to Room
            }
            chatUseCase.markMessagesAsRead(otherUserId, currentUserId)
        }
    }


    fun fetchMessages() {
        viewModelScope.launch {
            _messages.value = Resource.Loading
            try {
                chatUseCase.getChatMessages(currentUserId, otherUserId)
                    .collectLatest { list ->
                        _messages.postValue(Resource.Success(list))
                    }
            } catch (e: Exception) {
                _messages.postValue(Resource.Error("Failed to fetch messages", e))
            }
        }
    }

    fun sendMessage(text: String) {
        val message = MessageEntity(
            senderId = currentUserId,
            receiverId = otherUserId,
            text = text,
            timestamp = System.currentTimeMillis(),
            status = MessageStatus.PENDING
        )
        viewModelScope.launch(Dispatchers.IO) {
            chatUseCase.insertMessage(message.copy(status = MessageStatus.PENDING))
            val success = socketManager.sendMessage(message)
            val updatedMessage = message.copy(
                status = if (success) MessageStatus.SENT else MessageStatus.FAILED
            )
            chatUseCase.updateMessage(updatedMessage)
        }
    }

    /**
     * Retry failed messages when connection comes back or screen is resumed
     */
    fun retryFailedMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            val failedMessages =
                chatUseCase.getFailedMessages(currentUserId, otherUserId).first()
            failedMessages.forEach { msg ->
                val success = socketManager.sendMessage(msg)
                if (success) {
                    chatUseCase.updateMessage(msg.copy(status = MessageStatus.SENT))
                }
            }

        }
    }

    /**
     * Observe socket reconnects and trigger retry if needed
     */
    private fun observeSocketEvents() {
        socketManager.startObserving(viewModelScope)
        socketManager.setOnConnectedListener {
            retryFailedMessages()
        }
    }

    override fun onCleared() {
        super.onCleared()
        socketManager.disconnect()
    }
}

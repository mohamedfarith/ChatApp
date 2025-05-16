package com.learning.chatapp.data.usecaseimpl.chat

import com.learning.chatapp.data.local.entity.chat.MessageEntity
import com.learning.chatapp.domain.repository.chat.ChatRepository
import com.learning.chatapp.domain.usecase.chat.FetchChatUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchChatUseCaseImpl @Inject constructor(private val repo: ChatRepository) :
    FetchChatUseCase {
    override suspend fun getChatMessages(currentUserId: String, otherUserId: String) =
        repo.getMessagesBetween(currentUserId, otherUserId)

    override suspend fun insertMessage(message: MessageEntity) {
        repo.insertMessage(message)
    }

    override suspend fun updateMessage(message: MessageEntity) {
        repo.updateMessage(message)
    }

    override suspend fun getFailedMessages(
        currentUserId: String,
        otherUserId: String
    ): Flow<List<MessageEntity>> {
       return repo.getFailedMessages(currentUserId, otherUserId)
    }

    override suspend fun getLastMessageFromChat(): Flow<List<MessageEntity>> {
        return repo.getLastMessageFromChat()
    }

    override suspend fun markMessagesAsRead(otherUserId: String, currentUserId: String) {
        return repo.markMessagesAsRead(otherUserId, currentUserId)
    }


}
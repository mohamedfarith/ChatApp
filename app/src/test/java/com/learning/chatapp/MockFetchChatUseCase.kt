package com.learning.chatapp

import com.learning.chatapp.data.local.entity.chat.MessageEntity
import com.learning.chatapp.domain.models.MessageStatus
import com.learning.chatapp.domain.usecase.chat.FetchChatUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class MockFetchChatUseCase : FetchChatUseCase {
    override suspend fun getChatMessages(
        currentUserId: String,
        otherUserId: String
    ): Flow<List<MessageEntity>> {
       return flowOf(
            listOf(MessageEntity("1", "Me", "P1", "Hello", 1234567890, MessageStatus.SENT))
        )
    }

    override suspend fun insertMessage(message: MessageEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateMessage(message: MessageEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun getFailedMessages(
        currentUserId: String,
        otherUserId: String
    ): Flow<List<MessageEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun getLastMessageFromChat(): Flow<List<MessageEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun markMessagesAsRead(otherUserId: String, currentUserId: String) {
        TODO("Not yet implemented")
    }
}
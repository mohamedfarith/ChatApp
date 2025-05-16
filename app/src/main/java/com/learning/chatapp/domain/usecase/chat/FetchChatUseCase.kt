package com.learning.chatapp.domain.usecase.chat

import com.learning.chatapp.data.local.entity.chat.MessageEntity
import kotlinx.coroutines.flow.Flow

interface FetchChatUseCase {

    suspend fun getChatMessages(
        currentUserId: String,
        otherUserId: String
    ): Flow<List<MessageEntity>>

    suspend fun insertMessage(message: MessageEntity)

    suspend fun updateMessage(message: MessageEntity)

    suspend fun getFailedMessages(
        currentUserId: String,
        otherUserId: String
    ): Flow<List<MessageEntity>>

    suspend fun getLastMessageFromChat(): Flow<List<MessageEntity>>


    suspend fun markMessagesAsRead(otherUserId: String, currentUserId: String)
}
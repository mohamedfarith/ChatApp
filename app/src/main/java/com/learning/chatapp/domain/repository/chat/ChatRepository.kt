package com.learning.chatapp.domain.repository.chat

import com.learning.chatapp.data.local.entity.chat.MessageEntity
import com.learning.chatapp.data.local.entity.chat.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
interface ChatRepository {
    // Get all users from Room
    suspend fun getUsers(currentUserId: String): Flow<List<UserEntity>>

    // Insert default or new users
    suspend fun insertUsers(userEntities: List<UserEntity>)

    // Insert a message in DB
    suspend fun insertMessage(messageEntity: MessageEntity)

    // Get messages between two users (bidirectional)
    suspend fun getMessagesBetween(user1: String, user2: String): Flow<List<MessageEntity>>

    suspend fun updateMessage(message: MessageEntity)

    suspend fun getFailedMessages(
        currentUserId: String,
        otherUserId: String
    ): Flow<List<MessageEntity>>

    suspend fun getLastMessageFromChat(): Flow<List<MessageEntity>>

    suspend fun markMessagesAsRead(otherUserId: String, currentUserId: String)

}

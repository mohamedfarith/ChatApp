package com.learning.chatapp.data.repositoryImpl.chat

import android.util.Log
import com.learning.chatapp.Constants
import com.learning.chatapp.data.local.db.AppDatabase
import com.learning.chatapp.data.local.entity.chat.MessageEntity
import com.learning.chatapp.domain.models.MessageStatus
import com.learning.chatapp.domain.models.UserWithPreviewMessage
import com.learning.chatapp.data.local.entity.chat.UserEntity
import com.learning.chatapp.data.remote.socket.SocketManager
import com.learning.chatapp.domain.repository.chat.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val db: AppDatabase
) : ChatRepository {
    // Get all users from Room
    override suspend fun getUsers(currentUserId: String): Flow<List<UserEntity>> {
        val users = db.userDao().getAllUsers(currentUserId).first()
        if (users.isEmpty()) {
            val defaultUsers = listOf(
                UserEntity("Me","Person1"),
                UserEntity("P1", "Alice Johnson"),
                UserEntity("P2", "Michael Smith"),
                UserEntity("P3", "Ravi Patel"),
                UserEntity("P4", "Emma Williams"),
                UserEntity("P5", "Daniel Kim"),
                UserEntity("P6", "Priya Sharma"),
                UserEntity("P7", "James Anderson"),
                UserEntity("P8", "Sophia Chen"),
                UserEntity("P9", "Carlos Martinez"),
                UserEntity("P10", "Amina Khan")
            )
            db.userDao().insertUsers(defaultUsers)
        }
        return db.userDao().getAllUsers(currentUserId)
    }

    // Insert default or new users
    override suspend fun insertUsers(userEntities: List<UserEntity>) {
        db.userDao().insertUsers(userEntities)
    }

    // Insert a message in DB
    override suspend fun insertMessage(messageEntity: MessageEntity) {
        db.messageDao().insertMessage(messageEntity)
    }

    // Get messages between two users (bidirectional)
    override suspend fun getMessagesBetween(
        user1: String,
        user2: String
    ): Flow<List<MessageEntity>> =
        db.messageDao().getMessagesBetween(user1, user2)

    override suspend fun updateMessage(message: MessageEntity) {
        db.messageDao().insertMessage(message)
    }

    override suspend fun getFailedMessages(
        currentUserId: String,
        otherUserId: String
    ): Flow<List<MessageEntity>> {
        return db.messageDao().getFailedMessages(currentUserId, otherUserId)
    }

    override suspend fun getLastMessageFromChat(): Flow<List<MessageEntity>> {
        return db.messageDao().getLatestMessagesForChats(Constants.CURRENT_USER_ID)
    }

    override suspend fun markMessagesAsRead(otherUserId: String, currentUserId: String) {
        return db.messageDao().markMessagesAsRead(otherUserId, currentUserId)
    }

}
package com.learning.chatapp.data.local.entity.chat

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.learning.chatapp.domain.models.MessageStatus
import java.util.UUID

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val senderId: String,
    val receiverId: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: MessageStatus = MessageStatus.PENDING,
    val isRead: Boolean = false
)


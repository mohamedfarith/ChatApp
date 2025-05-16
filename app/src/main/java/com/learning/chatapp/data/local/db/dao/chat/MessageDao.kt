package com.learning.chatapp.data.local.db.dao.chat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.learning.chatapp.Constants
import com.learning.chatapp.data.local.entity.chat.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(messageEntity: MessageEntity)

    @Query("SELECT * FROM messages WHERE (senderId = :user1 AND receiverId = :user2) OR (senderId = :user2 AND receiverId = :user1) ORDER BY timestamp ASC")
    fun getMessagesBetween(user1: String, user2: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE senderId = :sender AND receiverId = :receiver AND status = 'FAILED'")
    fun getFailedMessages(sender: String, receiver: String): Flow<List<MessageEntity>>

    @Query("""
    SELECT * FROM messages
    WHERE timestamp IN (
        SELECT MAX(timestamp) FROM messages
        WHERE senderId = :userId OR receiverId = :userId
        GROUP BY 
            CASE 
                WHEN senderId = :userId THEN receiverId 
                ELSE senderId 
            END
    )
    ORDER BY timestamp DESC
""")
    fun getLatestMessagesForChats(userId:String=Constants.CURRENT_USER_ID): Flow<List<MessageEntity>>

    @Query("UPDATE messages SET isRead = 1 WHERE senderId = :otherUserId AND receiverId = :currentUserId AND isRead = 0")
    suspend fun markMessagesAsRead(otherUserId: String, currentUserId: String)

}
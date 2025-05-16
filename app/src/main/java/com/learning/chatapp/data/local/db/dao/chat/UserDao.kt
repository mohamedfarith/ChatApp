package com.learning.chatapp.data.local.db.dao.chat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.learning.chatapp.data.local.entity.chat.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(userEntities: List<UserEntity>)

    @Query("SELECT * FROM users WHERE id != :currentUserId")
    fun getAllUsers(currentUserId:String): Flow<List<UserEntity>>
}
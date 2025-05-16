package com.learning.chatapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.learning.chatapp.data.local.db.dao.chat.MessageDao
import com.learning.chatapp.data.local.db.dao.chat.UserDao
import com.learning.chatapp.data.local.entity.chat.MessageEntity
import com.learning.chatapp.data.local.entity.chat.UserEntity

@Database(entities = [UserEntity::class, MessageEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
}
package com.learning.chatapp.domain.usecase.chat

import com.learning.chatapp.data.local.entity.chat.UserEntity
import kotlinx.coroutines.flow.Flow

interface FetchUserUseCase {

    suspend fun getUsers(currentUserId:String): Flow<List<UserEntity>>
}
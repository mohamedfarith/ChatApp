package com.learning.chatapp.data.usecaseimpl.chat

import com.learning.chatapp.data.local.entity.chat.UserEntity
import com.learning.chatapp.domain.repository.chat.ChatRepository
import com.learning.chatapp.domain.usecase.chat.FetchUserUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchUserUseCaseImpl @Inject constructor(private val repo: ChatRepository) :
    FetchUserUseCase {
    override suspend fun getUsers(currentUserId:String): Flow<List<UserEntity>> {
        return repo.getUsers(currentUserId)
    }
}
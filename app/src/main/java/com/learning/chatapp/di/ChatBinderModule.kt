package com.learning.chatapp.di

import com.learning.chatapp.data.local.db.AppDatabase
import com.learning.chatapp.data.repositoryImpl.chat.ChatRepositoryImpl
import com.learning.chatapp.data.usecaseimpl.chat.FetchChatUseCaseImpl
import com.learning.chatapp.data.usecaseimpl.chat.FetchUserUseCaseImpl
import com.learning.chatapp.domain.repository.chat.ChatRepository
import com.learning.chatapp.domain.usecase.chat.FetchChatUseCase
import com.learning.chatapp.domain.usecase.chat.FetchUserUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ChatBinderModule {


    @Binds
    fun provideChatRepo(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository

    @Binds
    fun provideUserUseCase(useCaseImpl: FetchUserUseCaseImpl): FetchUserUseCase

    @Binds
    fun provideChatUseCase(useCaseImpl: FetchChatUseCaseImpl): FetchChatUseCase

}
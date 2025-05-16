package com.learning.chatapp.di

import android.content.Context
import androidx.compose.ui.unit.Constraints
import androidx.room.Room
import com.learning.chatapp.Constants
import com.learning.chatapp.DispatcherProvider
import com.learning.chatapp.data.remote.socket.SocketManager
import com.learning.chatapp.data.local.db.AppDatabase
import com.learning.chatapp.data.remote.socket.ConnectivityObserver
import com.learning.chatapp.data.repositoryImpl.chat.ChatRepositoryImpl
import com.learning.chatapp.domain.repository.chat.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "chat_app_db").build()


    @Provides
    @Singleton
    fun provideSocketManager(
        repository: ChatRepository,
        connectivityObserver: ConnectivityObserver,
    ): SocketManager = SocketManager(repository, connectivityObserver)

    @Provides
    @Singleton
    fun provideDispatcher(): DispatcherProvider = com.learning.chatapp.DefaultDispatcherProvider

    @Provides
    @Singleton
    fun provideChatRepositoryImpl(db: AppDatabase) =
        ChatRepositoryImpl(db)
}
package com.learning.chatapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import com.learning.chatapp.data.local.entity.chat.MessageEntity
import com.learning.chatapp.data.remote.socket.SocketManager
import com.learning.chatapp.domain.models.MessageStatus
import com.learning.chatapp.domain.usecase.chat.FetchChatUseCase
import com.learning.chatapp.presentation.chat.viewmodels.ChatViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class ChatViewModelTest {

    // Rule to run LiveData instantly on main thread
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test coroutine scheduler & dispatcher
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    // Test dispatcher provider for injecting in ViewModel
    private val testDispatcherProvider = object : DispatcherProvider {
        override val main = testDispatcher
        override val io = testDispatcher
        override val default = testDispatcher
        override val unconfined = testDispatcher
    }

    private lateinit var viewModel: ChatViewModel
    private lateinit var fetchChatUseCase: FetchChatUseCase
    private lateinit var socketManager: SocketManager
    private lateinit var savedStateHandle: SavedStateHandle
    private val fakeIncomingMessages = MutableSharedFlow<MessageEntity>()

    @Before
    fun setUp() {
        fetchChatUseCase = MockFetchChatUseCase()
        socketManager = mock()
        savedStateHandle = SavedStateHandle(mapOf("currentUserId" to "Me", "otherUserId" to "P1"))
        // Pass dispatcher provider to ViewModel constructor
        viewModel = ChatViewModel(fetchChatUseCase, socketManager, savedStateHandle, testDispatcherProvider)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `fetchMessages emits success`() = runTest(testScheduler) {
        val mockMessage = listOf(MessageEntity("1", "Me", "P1", "Hello", 1234567890, MessageStatus.SENT))

        // Mock use case to return flow emitting mock messages
        doReturn(fakeIncomingMessages).`when`(socketManager).incomingMessages
        whenever(fetchChatUseCase.getChatMessages("Me", "P1")).thenReturn(flowOf(mockMessage))

        val observer = Observer<Resource<List<MessageEntity>>> {}
        try {
            viewModel.messages.observeForever(observer)

            viewModel.fetchMessages()

            // Advance scheduler to execute all coroutines
            testScheduler.advanceUntilIdle()

            val result = viewModel.messages.value
            assertTrue(result is Resource.Success)
            assertEquals(mockMessage, (result as Resource.Success).data)
        } finally {
            viewModel.messages.removeObserver(observer)
        }
    }
}

package com.learning.chatapp.presentation.chat.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.chatapp.Constants
import com.learning.chatapp.Resource
import com.learning.chatapp.domain.models.UserWithPreviewMessage
import com.learning.chatapp.domain.usecase.chat.FetchChatUseCase
import com.learning.chatapp.domain.usecase.chat.FetchUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val repository: FetchUserUseCase,
    private val chatUseCase: FetchChatUseCase,
) : ViewModel() {


    private val _unreadMessages = MutableLiveData<Resource<List<UserWithPreviewMessage>>>()
    val unreadMessages: LiveData<Resource<List<UserWithPreviewMessage>>> = _unreadMessages

    fun getChatPreviews(){
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val unreadMessages = getUserWithUnreadPreviews(Constants.CURRENT_USER_ID).first()
                _unreadMessages.postValue(Resource.Success(unreadMessages))
            } catch (e: Exception) {
                _unreadMessages.postValue(Resource.Error(e.localizedMessage ?: "An error occurred"))
            }
        }
    }

    private suspend fun getUserWithUnreadPreviews(currentUserId: String): Flow<List<UserWithPreviewMessage>> {
        return combine(
            repository.getUsers(currentUserId),
            chatUseCase.getLastMessageFromChat()
        ) { users, messages ->
            users.map { user ->
                val message = messages.find {
                    (it.senderId == user.id && it.receiverId == currentUserId) ||
                            (it.senderId == currentUserId && it.receiverId == user.id)
                }

                UserWithPreviewMessage(
                    userId = user.id,
                    name = user.name,
                    latestMessage = message?.text,
                    timestamp = message?.timestamp
                )
            }
        }
    }
}
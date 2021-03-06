package com.example.messanger.presentation.fragment.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.model.MessageDto
import com.example.messanger.domain.model.UserDto
import com.example.messanger.domain.repository.IMessengerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel(
    private val messengerService: IMessengerService
): ViewModel() {
    private val _messageListFlow: MutableStateFlow<AsyncOperationResult<List<MessageDto>>> = MutableStateFlow(AsyncOperationResult.Loading())
    val messageListFlow: StateFlow<AsyncOperationResult<List<MessageDto>>> = _messageListFlow.asStateFlow()

    private val _companionFlow: MutableStateFlow<AsyncOperationResult<UserDto>> = MutableStateFlow(AsyncOperationResult.Loading())
    val companionFlow: StateFlow<AsyncOperationResult<UserDto>> = _companionFlow.asStateFlow()

    fun sendMessage(text: String, companionID: String) {
        viewModelScope.launch {
            if (text.isNotEmpty()) {
                val result = messengerService.sendMessage(text, companionID)
                _messageListFlow.value = result
                _messageListFlow.value = AsyncOperationResult.EmptyState()
            }
        }

        viewModelScope.launch {
            messageListFlow.collect {
                if (it is AsyncOperationResult.Success) messengerService.addChat(companionID, "chat")
            }
        }
    }

    fun getMessages(companionID: String) {
        viewModelScope.launch {
            val result = messengerService.getMessagesByCompanionId(companionID)
            result.collect {
                _messageListFlow.value = it
            }
            _messageListFlow.value = AsyncOperationResult.EmptyState()
        }
    }

    fun readMessage(companionID: String, messageId: String) {
        viewModelScope.launch {
            messengerService.readMessage(companionID, messageId)
        }
    }

    fun getCompanionById(companionID: String) {
        viewModelScope.launch {
            val result = messengerService.getCompanionById(companionID)
            _companionFlow.tryEmit(result)
        }
    }
}
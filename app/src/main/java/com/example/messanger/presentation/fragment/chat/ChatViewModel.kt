package com.example.messanger.presentation.fragment.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.model.MessageDto
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

    fun sendMessage(text: String, companionID: String) {
        viewModelScope.launch {
            if (text.isNotEmpty()) {
                val result = messengerService.sendMessage(text, companionID)
                _messageListFlow.value = result
                _messageListFlow.value = AsyncOperationResult.EmptyState()
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
}
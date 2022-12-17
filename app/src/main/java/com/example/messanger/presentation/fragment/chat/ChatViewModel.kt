package com.example.messanger.presentation.fragment.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messanger.core.extensions.mapIfSuccess
import com.example.messanger.core.result.OperationResult
import com.example.messanger.domain.model.Message
import com.example.messanger.domain.repository.IMessengerService
import com.example.messanger.presentation.model.MessageUi
import com.example.messanger.presentation.model.UserUi
import com.example.messanger.presentation.model.mapToUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val messengerService: IMessengerService
): ViewModel() {
    private val _messageListFlow: MutableStateFlow<OperationResult<List<MessageUi>>> =
        MutableStateFlow(
            OperationResult.Loading
        )
    val messageListFlow: StateFlow<OperationResult<List<MessageUi>>> =
        _messageListFlow.asStateFlow()

    private val _companionFlow: MutableStateFlow<OperationResult<UserUi>> = MutableStateFlow(
        OperationResult.Loading
    )
    val companionFlow: StateFlow<OperationResult<UserUi>> = _companionFlow.asStateFlow()

    fun sendMessage(text: String, companionID: String) {
        viewModelScope.launch {
            if (text.isNotEmpty()) {
                val result = messengerService.sendMessage(text, companionID)
                _messageListFlow.value = result.mapIfSuccess {
                    OperationResult.Success(it.map(Message::mapToUi))
                }
                _messageListFlow.value = OperationResult.Empty
            }
        }

        viewModelScope.launch {
            messageListFlow.collect {
                if (it is OperationResult.Success) messengerService.addChat(companionID, "chat")
            }
        }
    }

    fun getMessages(companionID: String) {
        viewModelScope.launch {
            val result = messengerService.getMessagesByCompanionId(companionID)
            result.collect {
                _messageListFlow.value = it.mapIfSuccess { list ->
                    OperationResult.Success(list.map(Message::mapToUi))
                }
            }
            _messageListFlow.value = OperationResult.Empty
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
            val companionUi = result.mapIfSuccess {
                OperationResult.Success(it.mapToUi())
            }
            _companionFlow.tryEmit(companionUi)
        }
    }
}
package com.example.messanger.presentation.fragment.chat

import androidx.lifecycle.viewModelScope
import com.example.messanger.core.extensions.mapIfSuccess
import com.example.messanger.core.result.ListResult
import com.example.messanger.core.result.OperationResult
import com.example.messanger.data.repository.IMessengerService
import com.example.messanger.presentation.fragment.base.BaseViewModel
import com.example.messanger.presentation.model.MessageUi
import com.example.messanger.presentation.model.UserUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    val companionID: String,
    private val messengerService: IMessengerService,
) : BaseViewModel() {
    private val _messageListFlow: MutableStateFlow<ListResult<MessageUi>> =
        MutableStateFlow(
            OperationResult.Loading
        )
    val messageListFlow: StateFlow<ListResult<MessageUi>> =
        _messageListFlow.asStateFlow()

    private val _companionFlow: MutableStateFlow<OperationResult<UserUi>> = MutableStateFlow(
        OperationResult.Loading
    )
    val companionFlow: StateFlow<OperationResult<UserUi>> = _companionFlow.asStateFlow()

    init {
        getCompanionById()
        getMessages()
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            if (text.isNotEmpty()) {
                val result = messengerService.sendMessage(text, companionID)
                _messageListFlow.value = result.mapIfSuccess {
                    OperationResult.Success(it)
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

    fun getMessages() {
        viewModelScope.launch {
            val result = messengerService.getMessagesByCompanionId(companionID)
            result.collect {
                _messageListFlow.value = it.mapIfSuccess { list ->
                    OperationResult.Success(list)
                }
            }
            _messageListFlow.value = OperationResult.Empty
        }
    }

    fun readMessage(messageId: String) {
        viewModelScope.launch {
            messengerService.readMessage(companionID, messageId)
        }
    }

    fun getCompanionById() {
        viewModelScope.launch {
            val result = messengerService.getCompanionById(companionID)
            val companionUi = result.mapIfSuccess {
                OperationResult.Success(it)
            }
            _companionFlow.tryEmit(companionUi)
        }
    }
}
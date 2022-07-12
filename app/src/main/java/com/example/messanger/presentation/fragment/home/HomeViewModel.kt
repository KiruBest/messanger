package com.example.messanger.presentation.fragment.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.core.UserState
import com.example.messanger.domain.model.ChatItemDto
import com.example.messanger.domain.model.UserDto
import com.example.messanger.domain.repository.IAccountService
import com.example.messanger.domain.repository.IMessengerService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val accountService: IAccountService,
    private val messengerService: IMessengerService
) : ViewModel() {
    private val _userDtoFlow =
        MutableStateFlow<AsyncOperationResult<UserDto>>(AsyncOperationResult.Loading())
    val userDtoFlow: StateFlow<AsyncOperationResult<UserDto>> = _userDtoFlow.asStateFlow()

    private val _logOutFlow =
        MutableStateFlow<AsyncOperationResult<Boolean>>(AsyncOperationResult.EmptyState())
    val logOutFlow: StateFlow<AsyncOperationResult<Boolean>> = _logOutFlow.asStateFlow()

    private val _usersListFlow = MutableStateFlow<AsyncOperationResult<List<UserDto>>>(AsyncOperationResult.Loading())
    val usersListFlow: StateFlow<AsyncOperationResult<List<UserDto>>> = _usersListFlow.asStateFlow()

    private val _chatListFlow = MutableStateFlow<AsyncOperationResult<List<ChatItemDto>>>(AsyncOperationResult.Loading())
    val chatListFlow: StateFlow<AsyncOperationResult<List<ChatItemDto>>> = _chatListFlow.asStateFlow()

    fun getCurrentUser() {
        viewModelScope.launch {
            val result = accountService.getCurrentUser()
            _userDtoFlow.tryEmit(result)
        }
    }

    fun logOut() {
        _logOutFlow.value = AsyncOperationResult.Loading()

        viewModelScope.launch {
            val result = accountService.logOut()
            _logOutFlow.value = result
        }
    }

    fun updateUserState(state: UserState) {
        viewModelScope.launch {
            accountService.updateUserState(state)
        }
    }

    fun getUsersList() {
        if (_usersListFlow.value !is AsyncOperationResult.Loading) {
            _usersListFlow.value = AsyncOperationResult.Loading()
        }

        viewModelScope.launch {
            val result = messengerService.getUsersList()
            _usersListFlow.value = result
            _usersListFlow.value = AsyncOperationResult.EmptyState()
        }
    }

    fun getChats() {
        viewModelScope.launch {
            if (_chatListFlow.value !is AsyncOperationResult.Loading) {
                _chatListFlow.value = AsyncOperationResult.Loading()
            }

            val result = messengerService.getExistsChats()

            result.collect {
                _chatListFlow.value = it
            }

            _chatListFlow.value = AsyncOperationResult.EmptyState()
        }
    }

    fun filter(newText: String?) {
        val result = messengerService.searchUser(newText)
        _usersListFlow.value = AsyncOperationResult.Success(result)
    }
}
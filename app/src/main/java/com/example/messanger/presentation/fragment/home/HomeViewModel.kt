package com.example.messanger.presentation.fragment.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.core.UserState
import com.example.messanger.domain.model.UserDto
import com.example.messanger.domain.repository.IAccountService
import com.example.messanger.domain.repository.IMessengerService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        viewModelScope.launch {
            val result = messengerService.getUsersList()
            _usersListFlow.value = result
        }
    }
}
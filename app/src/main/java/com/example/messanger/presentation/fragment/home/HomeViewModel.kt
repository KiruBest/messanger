package com.example.messanger.presentation.fragment.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.core.UserState
import com.example.messanger.domain.model.UserDto
import com.example.messanger.domain.repository.IAccountService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val accountService: IAccountService
) : ViewModel() {
    private val _userDtoFlow =
        MutableStateFlow<AsyncOperationResult<UserDto>>(AsyncOperationResult.Loading())
    val userDtoFlow: StateFlow<AsyncOperationResult<UserDto>> = _userDtoFlow.asStateFlow()

    fun getCurrentUser() {
        viewModelScope.launch {
            val result = accountService.getCurrentUser()
            _userDtoFlow.tryEmit(result)
        }
    }

    fun logOut() {
        accountService.logOut()
    }

    fun updateUserState(state: UserState) {
        viewModelScope.launch {
            accountService.updateUserState(state)
        }
    }
}
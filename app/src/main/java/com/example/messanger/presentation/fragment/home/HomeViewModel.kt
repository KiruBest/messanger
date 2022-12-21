package com.example.messanger.presentation.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messanger.core.enumeration.UserState
import com.example.messanger.core.extensions.mapIfSuccess
import com.example.messanger.core.result.ListResult
import com.example.messanger.core.result.OperationResult
import com.example.messanger.domain.model.ChatItemDto
import com.example.messanger.domain.model.User
import com.example.messanger.domain.repository.IAccountService
import com.example.messanger.domain.repository.IMessengerService
import com.example.messanger.presentation.model.UserUi
import com.example.messanger.presentation.model.mapToUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val accountService: IAccountService,
    private val messengerService: IMessengerService
) : ViewModel() {
    private val _userDtoFlow =
        MutableStateFlow<OperationResult<UserUi>>(OperationResult.Loading)
    val userDtoFlow: StateFlow<OperationResult<UserUi>> = _userDtoFlow.asStateFlow()

    private val _logOutFlow =
        MutableStateFlow<OperationResult<Boolean>>(OperationResult.Empty)
    val logOutFlow: StateFlow<OperationResult<Boolean>> = _logOutFlow.asStateFlow()

    private val _usersListFlow = MutableStateFlow<ListResult<UserUi>>(OperationResult.Loading)
    val usersListFlow: StateFlow<ListResult<UserUi>> = _usersListFlow.asStateFlow()

    private val _chatListFlow =
        MutableLiveData<OperationResult<List<ChatItemDto>>>(OperationResult.Loading)
    val chatListFlow: LiveData<OperationResult<List<ChatItemDto>>> get() = _chatListFlow

    init {
        getCurrentUser()
        getUsersList()
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            val result = accountService.getCurrentUser().mapIfSuccess {
                OperationResult.Success(it.mapToUi())
            }
            _userDtoFlow.tryEmit(result)
        }
    }

    fun logOut() {
        _logOutFlow.value = OperationResult.Loading

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
            if (_usersListFlow.value !is OperationResult.Loading) {
                _usersListFlow.value = OperationResult.Loading
            }

            val result = messengerService.getUsersList().mapIfSuccess { list ->
                OperationResult.Success(list.map(User::mapToUi))
            }
            _usersListFlow.value = result
        }
    }

    fun getChats() {
        viewModelScope.launch {
            if (_chatListFlow.value !is OperationResult.Loading) {
                _chatListFlow.value = OperationResult.Loading
            }

            val result = messengerService.getExistsChats()

            result.collect {
                _chatListFlow.value = it
            }

            _chatListFlow.value = OperationResult.Empty
        }
    }

    fun filter(newText: String?) {
        viewModelScope.launch {
            val result = messengerService.searchUser(newText)
                .mapIfSuccess { OperationResult.Success(it.map((User::mapToUi))) }
            _usersListFlow.value = result
        }
    }
}
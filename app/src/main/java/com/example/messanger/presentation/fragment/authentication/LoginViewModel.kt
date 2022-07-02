package com.example.messanger.presentation.fragment.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.repository.IAccountService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val accountService: IAccountService
) : ViewModel() {
    private val _loginFlow: MutableStateFlow<AsyncOperationResult<Boolean>> = MutableStateFlow(AsyncOperationResult.Loading())
    val loginFlow: StateFlow<AsyncOperationResult<Boolean>> = _loginFlow.asStateFlow()

    private val _codeSentFlow: MutableStateFlow<AsyncOperationResult<Boolean>> = MutableStateFlow(AsyncOperationResult.Loading())
    val codeSentFlow: StateFlow<AsyncOperationResult<Boolean>> = _loginFlow.asStateFlow()

    fun performPhoneAuth(phoneNumber: String) {
        viewModelScope.launch {
            val result = accountService.performPhoneAuth(phoneNumber)
            _loginFlow.tryEmit(result)
        }
    }

    fun sentAuthCode(code: String) {
        viewModelScope.launch {
            val result = accountService.sentAuthCode(code)
            _codeSentFlow.tryEmit(result)
        }
    }
}
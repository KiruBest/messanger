package com.example.messanger.presentation.fragment.authentication

import androidx.lifecycle.viewModelScope
import com.example.messanger.core.result.OperationResult
import com.example.messanger.data.repository.IAccountService
import com.example.messanger.presentation.fragment.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val accountService: IAccountService
) : BaseViewModel() {
    private val _loginFlow: MutableStateFlow<OperationResult<Boolean>> = MutableStateFlow(
        OperationResult.Empty
    )
    val loginFlow: StateFlow<OperationResult<Boolean>> = _loginFlow.asStateFlow()

    private val _codeSentFlow: MutableStateFlow<OperationResult<Boolean>> = MutableStateFlow(
        OperationResult.Empty
    )
    val codeSentFlow: StateFlow<OperationResult<Boolean>> = _codeSentFlow.asStateFlow()

    private val _userAuthFlow: MutableStateFlow<Boolean> =
        MutableStateFlow(accountService.userAuthCheck())
    val userAuthFlow: StateFlow<Boolean> = _userAuthFlow.asStateFlow()

    fun performPhoneAuth(phoneNumber: String) {
        _loginFlow.tryEmit(OperationResult.Loading)

        viewModelScope.launch {
            val result = accountService.performPhoneAuth(phoneNumber)
            _loginFlow.tryEmit(result)
        }
    }

    fun sentAuthCode(code: String) {
        _codeSentFlow.tryEmit(OperationResult.Loading)

        viewModelScope.launch {
            val result = accountService.sentAuthCode(code)
            _codeSentFlow.tryEmit(result)
        }
    }
}
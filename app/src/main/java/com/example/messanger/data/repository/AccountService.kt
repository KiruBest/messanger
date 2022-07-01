package com.example.messanger.data.repository

import com.example.messanger.data.core.PhoneAuthResult
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.repository.IAccountService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountService(
    private val accountSource: IAccountSource
): IAccountService {
    override suspend fun performPhoneAuth(
        phoneNumber: String,
        password: String
    ): AsyncOperationResult<Boolean> = withContext(Dispatchers.IO) {
        when(val result = accountSource.performPhoneAuth(phoneNumber, password)) {
            is PhoneAuthResult.VerificationCompleted -> {
                AsyncOperationResult.Success(true)
            }
            is PhoneAuthResult.Failure -> {
                AsyncOperationResult.Failure(result.exception)
            }
            is PhoneAuthResult.CodeSent -> {
                AsyncOperationResult.Loading()
            }
        }
    }
}
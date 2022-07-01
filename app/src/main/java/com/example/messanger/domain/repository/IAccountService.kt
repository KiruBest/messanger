package com.example.messanger.domain.repository

import com.example.messanger.domain.core.AsyncOperationResult

interface IAccountService {
    suspend fun performPhoneAuth(phoneNumber: String, password: String): AsyncOperationResult<Boolean>
}
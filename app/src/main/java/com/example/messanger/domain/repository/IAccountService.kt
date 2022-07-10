package com.example.messanger.domain.repository

import android.graphics.Bitmap
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.core.UserState
import com.example.messanger.domain.model.UserDto

interface IAccountService {
    suspend fun performPhoneAuth(phoneNumber: String): AsyncOperationResult<Boolean>
    suspend fun sentAuthCode(code: String): AsyncOperationResult<Boolean>
    suspend fun getCurrentUser(): AsyncOperationResult<UserDto>
    suspend fun updateUserParams(userDto: UserDto, bitmap: Bitmap? = null): AsyncOperationResult<Boolean>
    suspend fun updateUserState(state: UserState): AsyncOperationResult<UserDto>
    suspend fun logOut(): AsyncOperationResult<Boolean>
    fun userAuthCheck(): Boolean
}
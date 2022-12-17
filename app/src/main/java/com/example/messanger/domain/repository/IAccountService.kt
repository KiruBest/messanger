package com.example.messanger.domain.repository

import android.graphics.Bitmap
import com.example.messanger.core.enumeration.UserState
import com.example.messanger.core.result.OperationResult
import com.example.messanger.domain.model.User

interface IAccountService {
    suspend fun performPhoneAuth(phoneNumber: String): OperationResult<Boolean>
    suspend fun sentAuthCode(code: String): OperationResult<Boolean>
    suspend fun getCurrentUser(): OperationResult<User>
    suspend fun updateUserParams(userDto: User, bitmap: Bitmap? = null): OperationResult<Boolean>
    suspend fun updateUserState(state: UserState): OperationResult<User>
    suspend fun logOut(): OperationResult<Boolean>
    suspend fun setUserAccountStatus(text: String): OperationResult<String>
    fun userAuthCheck(): Boolean
}
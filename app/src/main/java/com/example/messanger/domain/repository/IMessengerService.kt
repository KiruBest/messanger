package com.example.messanger.domain.repository

import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.model.UserDto

interface IMessengerService {
    suspend fun getUsersList(): AsyncOperationResult<List<UserDto>>
}
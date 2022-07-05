package com.example.messanger.domain.repository

import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.model.MessageDto
import com.example.messanger.domain.model.UserDto

interface IMessengerService {
    suspend fun getUsersList(): AsyncOperationResult<List<UserDto>>
    suspend fun sendMessage(text: String, companionID: String): AsyncOperationResult<List<MessageDto>>
    suspend fun getMessages(): AsyncOperationResult<List<MessageDto>>
    fun searchUser(newText: String?): List<UserDto>
}
package com.example.messanger.domain.repository

import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.model.ChatItemDto
import com.example.messanger.domain.model.MessageDto
import com.example.messanger.domain.model.UserDto
import kotlinx.coroutines.flow.Flow

interface IMessengerService {
    suspend fun getUsersList(): AsyncOperationResult<List<UserDto>>
    suspend fun sendMessage(text: String, companionID: String): AsyncOperationResult<List<MessageDto>>
    suspend fun getMessagesByCompanionId(companionID: String): Flow<AsyncOperationResult<List<MessageDto>>>
    suspend fun getExistsChats(): Flow<AsyncOperationResult<List<ChatItemDto>>>
    suspend fun addChat(companionID: String, chatType: String): AsyncOperationResult<Boolean>
    suspend fun readMessage(companionID: String, messageID: String)
    suspend fun getCompanionById(companionID: String): AsyncOperationResult<UserDto>
    fun searchUser(newText: String?): List<UserDto>
}
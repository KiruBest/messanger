package com.example.messanger.domain.repository

import com.example.messanger.core.result.FlowListResult
import com.example.messanger.core.result.ListResult
import com.example.messanger.core.result.OperationResult
import com.example.messanger.domain.model.ChatItemDto
import com.example.messanger.domain.model.Message
import com.example.messanger.domain.model.User

interface IMessengerService {
    suspend fun getUsersList(): ListResult<User>
    suspend fun sendMessage(text: String, companionID: String): ListResult<Message>
    suspend fun getMessagesByCompanionId(companionID: String): FlowListResult<Message>
    suspend fun getExistsChats(): FlowListResult<ChatItemDto>
    suspend fun addChat(companionID: String, chatType: String): OperationResult<Boolean>
    suspend fun readMessage(companionID: String, messageID: String): OperationResult<Unit>
    suspend fun getCompanionById(companionID: String): OperationResult<User>
    suspend fun searchUser(newText: String?): ListResult<User>
}
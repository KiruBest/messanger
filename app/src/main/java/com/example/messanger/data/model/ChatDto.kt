package com.example.messanger.data.model

import com.example.messanger.domain.model.Chat

data class ChatDto(
    val id: String,
    val type: String
)

fun ChatDto.mapToDomain() = Chat(
    id, type
)

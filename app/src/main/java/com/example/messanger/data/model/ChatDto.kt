package com.example.messanger.data.model

import com.example.messanger.presentation.model.ChatUi

data class ChatDto(
    val id: String,
    val type: String
)

fun ChatDto.mapToUi() = ChatUi(
    id = id,
    type = type
)

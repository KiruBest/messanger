package com.example.messanger.presentation.model

import com.example.messanger.domain.model.Chat

data class ChatUi(
    val id: String,
    val type: String
)

fun Chat.mapToUi() = ChatUi(id, type)
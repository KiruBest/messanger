package com.example.messanger.presentation.model

import com.example.messanger.domain.model.Message

data class MessageUi(
    val id: String,
    val text: String,
    val type: String,
    val from: String,
    val timestamp: Long,
    val seen: Boolean
)

fun Message.mapToUi() = MessageUi(id, text, type, from, timestamp, seen)
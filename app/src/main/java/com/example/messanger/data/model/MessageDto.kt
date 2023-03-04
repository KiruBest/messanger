package com.example.messanger.data.model

import com.example.messanger.presentation.model.MessageUi

data class MessageDto(
    val id: String,
    val text: String,
    val type: String,
    val from: String,
    val timestamp: Long,
    val seen: Boolean
)

fun MessageDto.mapToUi() = MessageUi(
    id = id,
    text = text,
    type = type,
    from = from,
    timestamp = timestamp,
    seen = seen
)
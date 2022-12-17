package com.example.messanger.data.model

import com.example.messanger.domain.model.Message

data class MessageDto(
    val id: String,
    val text: String,
    val type: String,
    val from: String,
    val timestamp: Long,
    val seen: Boolean
)

fun MessageDto.mapToDomain() = Message(
    id, text, type, from, timestamp, seen
)
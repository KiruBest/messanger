package com.example.messanger.domain.model

data class MessageDto(
    val id: String,
    var text: String,
    var type: String,
    val from: String,
    val timestamp: Long
)

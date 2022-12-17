package com.example.messanger.domain.model

data class Message(
    val id: String,
    val text: String,
    val type: String,
    val from: String,
    val timestamp: Long,
    val seen: Boolean
)

package com.example.messanger.presentation.model

data class MessageUi(
    val id: String,
    val text: String,
    val type: String,
    val from: String,
    val timestamp: Long,
    val seen: Boolean
)
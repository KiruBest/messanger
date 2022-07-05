package com.example.messanger.domain.model

data class MessageDto(
    var text: String = "",
    var type: String = "",
    val from: String = "",
    val timestamp: String = ""
)

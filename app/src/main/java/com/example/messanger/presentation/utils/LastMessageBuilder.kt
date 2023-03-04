package com.example.messanger.presentation.utils

import com.example.messanger.data.model.ChatItemDto

//todo Удалить эту дичь, вынести в ChatItemDto
class LastMessageBuilder(private val chatItemDto: ChatItemDto) {
    fun getLastMessageSingleChat(): String {
        var lastMessage = chatItemDto.text

        if (lastMessage.length > 26) lastMessage = lastMessage.substring(0, 26) + "..."

        return if (chatItemDto.userID == chatItemDto.from) {
            lastMessage
        } else {
            "Вы: $lastMessage"
        }
    }
}
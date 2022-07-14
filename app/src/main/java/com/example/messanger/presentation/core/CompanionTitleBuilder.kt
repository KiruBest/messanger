package com.example.messanger.presentation.core

import android.content.Context
import com.example.messanger.R
import com.example.messanger.domain.model.ChatItemDto
import com.example.messanger.domain.model.UserDto

class CompanionTitleBuilder(
    private val userDto: UserDto,
    private val context: Context
) {
    fun getTitle(): String = if (userDto.fName != "" || userDto.lName != "") {
        context.getString(R.string.full_name, userDto.fName, userDto.lName)
    } else userDto.phone
}

class CompanionTitleBuilderFromChatItemDto(
    private val userDto: ChatItemDto,
    private val context: Context
) {
    fun getTitle(): String = if (userDto.fName != "" || userDto.lName != "") {
        context.getString(R.string.full_name, userDto.fName, userDto.lName)
    } else userDto.phone
}
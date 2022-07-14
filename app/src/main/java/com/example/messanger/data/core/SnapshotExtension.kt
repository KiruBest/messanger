package com.example.messanger.data.core

import com.example.messanger.data.core.Constants.CHAT_TYPE
import com.example.messanger.data.core.Constants.MESSAGE_FROM
import com.example.messanger.data.core.Constants.MESSAGE_ID
import com.example.messanger.data.core.Constants.MESSAGE_NO_SEEN_COUNT
import com.example.messanger.data.core.Constants.MESSAGE_SEEN
import com.example.messanger.data.core.Constants.MESSAGE_TEXT
import com.example.messanger.data.core.Constants.MESSAGE_TIMESTAMP
import com.example.messanger.data.core.Constants.MESSAGE_TYPE
import com.example.messanger.data.core.Constants.USER_AVATAR_URL
import com.example.messanger.data.core.Constants.USER_F_NAME
import com.example.messanger.data.core.Constants.USER_ID
import com.example.messanger.data.core.Constants.USER_L_NAME
import com.example.messanger.data.core.Constants.USER_PHONE
import com.example.messanger.data.core.Constants.USER_STATUS
import com.example.messanger.data.core.Constants.USER_USERNAME
import com.example.messanger.domain.model.ChatDto
import com.example.messanger.domain.model.ChatItemDto
import com.example.messanger.domain.model.MessageDto
import com.example.messanger.domain.model.UserDto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.getValue

fun DataSnapshot.mapToUserDto() = UserDto(
    id = child(USER_ID).getValue<String>() ?: "",
    username = child(USER_USERNAME).getValue<String>() ?: "",
    fName = child(USER_F_NAME).getValue<String>() ?: "",
    lName = child(USER_L_NAME).getValue<String>() ?: "",
    status = child(USER_STATUS).getValue<String>() ?: "",
    avatarUrl = child(USER_AVATAR_URL).getValue<String>() ?: "",
    phone = child(USER_PHONE).getValue<String>() ?: ""
)

fun DataSnapshot.mapToMessageDto() = MessageDto(
    id = child(MESSAGE_ID).getValue<String>() ?: "",
    text = child(MESSAGE_TEXT).getValue<String>() ?: "",
    type = child(MESSAGE_TYPE).getValue<String>() ?: "",
    from = child(MESSAGE_FROM).getValue<String>() ?: "",
    timestamp = child(MESSAGE_TIMESTAMP).getValue<Long>() ?: -1,
    seen = child(MESSAGE_SEEN).getValue<Boolean>() ?: true
)

fun DataSnapshot.mapToChatItemDto() = ChatItemDto(
    userID = child(USER_ID).getValue<String>() ?: "",
    username = child(USER_USERNAME).getValue<String>() ?: "",
    fName = child(USER_F_NAME).getValue<String>() ?: "",
    lName = child(USER_L_NAME).getValue<String>() ?: "",
    status = child(USER_STATUS).getValue<String>() ?: "",
    avatarUrl = child(USER_AVATAR_URL).getValue<String>() ?: "",
    phone = child(USER_PHONE).getValue<String>() ?: "",
    messageID = child(MESSAGE_ID).getValue<String>() ?: "",
    text = child(MESSAGE_TEXT).getValue<String>() ?: "",
    type = child(MESSAGE_TYPE).getValue<String>() ?: "",
    from = child(MESSAGE_FROM).getValue<String>() ?: "",
    timestamp = child(MESSAGE_TIMESTAMP).getValue<Long>() ?: -1,
    seen = child(MESSAGE_SEEN).getValue<Boolean>() ?: true,
    noSeenMessageCount = child(MESSAGE_NO_SEEN_COUNT).getValue<Int>() ?: 0
)

fun DataSnapshot.mapToChatDto() = ChatDto(
    id = child(USER_ID).getValue<String>() ?: "",
    type = child(CHAT_TYPE).getValue<String>() ?: ""
)
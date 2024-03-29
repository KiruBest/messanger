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
import com.example.messanger.data.core.Constants.USER_DATA_BIRTH
import com.example.messanger.data.core.Constants.USER_F_NAME
import com.example.messanger.data.core.Constants.USER_ID
import com.example.messanger.data.core.Constants.USER_L_NAME
import com.example.messanger.data.core.Constants.USER_M_NAME
import com.example.messanger.data.core.Constants.USER_PHONE
import com.example.messanger.data.core.Constants.USER_STATUS
import com.example.messanger.data.core.Constants.USER_USERNAME
import com.example.messanger.data.model.ChatDto
import com.example.messanger.data.model.ChatItemDto
import com.example.messanger.data.model.MessageDto
import com.example.messanger.data.model.UserDto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.getValue

fun DataSnapshot.mapToUserDto() = UserDto(
    id = child(USER_ID).getValue<String>().orEmpty(),
    username = child(USER_USERNAME).getValue<String>().orEmpty(),
    fName = child(USER_F_NAME).getValue<String>().orEmpty(),
    mName = child(USER_M_NAME).getValue<String>().orEmpty(),
    lName = child(USER_L_NAME).getValue<String>().orEmpty(),
    status = child(USER_STATUS).getValue<String>().orEmpty(),
    avatarUrl = child(USER_AVATAR_URL).getValue<String>().orEmpty(),
    phone = child(USER_PHONE).getValue<String>().orEmpty(),
    dataBirth = child(USER_DATA_BIRTH).getValue<String>().orEmpty()
)

fun DataSnapshot.mapToMessageDto() = MessageDto(
    id = child(MESSAGE_ID).getValue<String>().orEmpty(),
    text = child(MESSAGE_TEXT).getValue<String>().orEmpty(),
    type = child(MESSAGE_TYPE).getValue<String>().orEmpty(),
    from = child(MESSAGE_FROM).getValue<String>().orEmpty(),
    timestamp = child(MESSAGE_TIMESTAMP).getValue<Long>() ?: -1,
    seen = child(MESSAGE_SEEN).getValue<Boolean>() ?: true
)

fun DataSnapshot.mapToChatItemDto() = ChatItemDto(
    userID = child(USER_ID).getValue<String>().orEmpty(),
    username = child(USER_USERNAME).getValue<String>().orEmpty(),
    fName = child(USER_F_NAME).getValue<String>().orEmpty(),
    mName = child(USER_M_NAME).getValue<String>().orEmpty(),
    lName = child(USER_L_NAME).getValue<String>().orEmpty(),
    status = child(USER_STATUS).getValue<String>().orEmpty(),
    avatarUrl = child(USER_AVATAR_URL).getValue<String>().orEmpty(),
    phone = child(USER_PHONE).getValue<String>().orEmpty(),
    messageID = child(MESSAGE_ID).getValue<String>().orEmpty(),
    text = child(MESSAGE_TEXT).getValue<String>().orEmpty(),
    type = child(MESSAGE_TYPE).getValue<String>().orEmpty(),
    from = child(MESSAGE_FROM).getValue<String>().orEmpty(),
    timestamp = child(MESSAGE_TIMESTAMP).getValue<Long>() ?: -1,
    seen = child(MESSAGE_SEEN).getValue<Boolean>() ?: true,
    noSeenMessageCount = child(MESSAGE_NO_SEEN_COUNT).getValue<Int>() ?: 0,
    dataBirth = child(USER_DATA_BIRTH).getValue<String>().orEmpty()
)

fun DataSnapshot.mapToChatDto() = ChatDto(
    id = child(USER_ID).getValue<String>().orEmpty(),
    type = child(CHAT_TYPE).getValue<String>().orEmpty()
)
package com.example.messanger.data.core

import com.example.messanger.data.core.Constants.USER_AVATAR_URL
import com.example.messanger.data.core.Constants.USER_F_NAME
import com.example.messanger.data.core.Constants.USER_ID
import com.example.messanger.data.core.Constants.USER_L_NAME
import com.example.messanger.data.core.Constants.USER_PHONE
import com.example.messanger.data.core.Constants.USER_STATUS
import com.example.messanger.data.core.Constants.USER_USERNAME
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
    phone = child(USER_PHONE).getValue<String>() ?: "",
)
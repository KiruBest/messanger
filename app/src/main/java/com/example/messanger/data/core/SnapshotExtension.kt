package com.example.messanger.data.core

import com.example.messanger.domain.model.UserDto
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.getValue

fun DataSnapshot.mapToUserDto() = UserDto(
    id = child("id").getValue<String>() ?: "",
    username = child("username").getValue<String>() ?: "",
    fName = child("fName").getValue<String>() ?: "",
    lName = child("lName").getValue<String>() ?: "",
    status = child("status").getValue<String>() ?: "",
    avatarUrl = child("avatarUrl").getValue<String>() ?: "",
    phone = child("phone").getValue<String>() ?: "",
)
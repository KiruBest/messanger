package com.example.messanger.data.model

import com.example.messanger.domain.model.User
import java.io.Serializable

data class UserDto(
    val id: String,
    val username: String,
    val fName: String,
    val mName: String,
    val lName: String,
    val status: String,
    val avatarUrl: String,
    val dataBirth: String,
    val phone: String
) : Serializable

fun UserDto.mapToDomain() = User(
    id, username, fName, mName, lName, status, avatarUrl, dataBirth, phone
)

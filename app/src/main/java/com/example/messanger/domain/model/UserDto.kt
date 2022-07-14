package com.example.messanger.domain.model

import java.io.Serializable

data class UserDto(
    val id: String,
    var username: String,
    var fName: String,
    var lName: String,
    var status: String,
    var avatarUrl: String,
    var phone: String
): Serializable

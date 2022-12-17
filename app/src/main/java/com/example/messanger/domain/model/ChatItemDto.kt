package com.example.messanger.domain.model

import com.example.messanger.data.model.UserDto

data class ChatItemDto(
    val messageID: String,
    val text: String,
    val type: String,
    val from: String,
    val timestamp: Long,
    val userID: String,
    val username: String,
    val fName: String,
    val mName: String,
    val lName: String,
    val status: String,
    val avatarUrl: String,
    val phone: String,
    val seen: Boolean,
    val noSeenMessageCount: Int,
    val dataBirth: String
) {
    fun mapToUserDto() =
        UserDto(userID, username, fName, mName, lName, status, avatarUrl, phone, dataBirth)

    val userFullName
        get() = if (fName.isNotBlank() || lName.isNotBlank()) {
            "$fName $lName"
        } else {
            phone
        }
}

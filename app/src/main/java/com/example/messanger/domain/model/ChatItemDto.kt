package com.example.messanger.domain.model

data class ChatItemDto(
    val messageID: String,
    var text: String,
    var type: String,
    val from: String,
    val timestamp: Long,
    val userID: String,
    var username: String,
    var fName: String,
    var lName: String,
    var status: String,
    var avatarUrl: String,
    var phone: String
) {
    fun mapToUserDto(): UserDto {
        return UserDto(userID, username, fName, lName, status, avatarUrl, phone)
    }
}

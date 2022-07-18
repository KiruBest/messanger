package com.example.messanger.domain.model

data class ChatItemDto(
    var messageID: String,
    var text: String,
    var type: String,
    var from: String,
    var timestamp: Long,
    val userID: String,
    var username: String,
    var fName: String,
    var mName: String,
    var lName: String,
    var status: String,
    var avatarUrl: String,
    var phone: String,
    var seen: Boolean,
    var noSeenMessageCount: Int,
    var dataBirth: String
) {
    fun mapToUserDto() = UserDto(userID, username, fName,mName, lName, status, avatarUrl, phone,dataBirth)
}

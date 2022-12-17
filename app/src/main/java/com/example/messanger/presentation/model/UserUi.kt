package com.example.messanger.presentation.model

import com.example.messanger.domain.model.User

data class UserUi(
    val id: String,
    val username: String,
    val fName: String,
    val mName: String,
    val lName: String,
    val status: String,
    val avatarUrl: String,
    val dataBirth: String,
    val phone: String
) {
    val fullName
        get() = if (fName.isNotBlank() || lName.isNotBlank()) {
            "$fName $lName"
        } else {
            phone
        }
}

fun User.mapToUi() = UserUi(id, username, fName, mName, lName, status, avatarUrl, dataBirth, phone)
fun UserUi.mapToDomain() =
    User(id, username, fName, mName, lName, status, avatarUrl, dataBirth, phone)
package com.example.messanger.presentation.model

import com.example.messanger.core.enumeration.UserState
import java.io.Serializable

data class UserUi(
    val id: String,
    val username: String,
    val fName: String,
    val mName: String,
    val lName: String,
    val status: UserState,
    val avatarUrl: String,
    val dataBirth: String,
    val phone: String
) : Serializable {
    val fullName
        get() = if (fName.isNotBlank() || lName.isNotBlank()) {
            "$fName $lName"
        } else {
            phone
        }
}
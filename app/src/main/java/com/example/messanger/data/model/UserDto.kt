package com.example.messanger.data.model

import com.example.messanger.core.enumeration.UserState
import com.example.messanger.presentation.model.UserUi
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

fun UserDto.mapToUi(): UserUi {
    val userState = UserState.mapToUserState(status)

    return UserUi(
        id = id,
        username = username,
        fName = fName,
        mName = mName,
        lName = lName,
        status = userState,
        avatarUrl = avatarUrl,
        dataBirth = dataBirth,
        phone = phone
    )
}

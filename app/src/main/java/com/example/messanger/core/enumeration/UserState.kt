package com.example.messanger.core.enumeration

enum class UserState(val status: String) {
    ONLINE("Онлайн"),
    OFFLINE("Был недавно"),
    EMPTY("");

    companion object {
        fun mapToUserState(status: String) = when (status) {
            ONLINE.status -> ONLINE
            OFFLINE.status -> OFFLINE
            else -> EMPTY
        }
    }
}
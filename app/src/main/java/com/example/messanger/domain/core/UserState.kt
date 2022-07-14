package com.example.messanger.domain.core

enum class UserState(val state: String) {
    ONLINE("Онлайн"),
    OFFLINE("Был недавно"),
    TYPING("Печатает...");
}
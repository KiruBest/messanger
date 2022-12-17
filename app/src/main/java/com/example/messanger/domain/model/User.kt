package com.example.messanger.domain.model

data class User(
    val id: String,
    val username: String,
    val fName: String,
    val mName: String,
    val lName: String,
    val status: String,
    val avatarUrl: String,
    val dataBirth: String,
    val phone: String
)

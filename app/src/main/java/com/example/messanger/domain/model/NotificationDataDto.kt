package com.example.messanger.domain.model

import com.google.gson.annotations.SerializedName

data class NotificationDataDto(
    val body: String,
    @SerializedName("companion_id") val companionID: String,
    val photo: String,
    val title: String
)
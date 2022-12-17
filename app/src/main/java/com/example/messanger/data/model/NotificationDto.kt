package com.example.messanger.data.model

import com.google.gson.annotations.SerializedName

data class NotificationDto(
    @SerializedName("data") val notificationNotificationDataDto: NotificationDataDto,
    val to: String
)
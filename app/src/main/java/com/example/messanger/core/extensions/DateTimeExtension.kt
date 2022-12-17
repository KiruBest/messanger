package com.example.messanger.presentation.utils

import java.text.SimpleDateFormat
import java.util.*

fun Long.asDatHourMinute(): String {
    val date = Date(this)
    val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return simpleDateFormat.format(date)
}
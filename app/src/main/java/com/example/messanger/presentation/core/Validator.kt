package com.example.messanger.presentation.core

fun String.validateNumber(): String {
    return when {
        this.isEmpty() -> "Необходимов ввести номер"
        else -> ""
    }
}

fun String.validateCode(): String {
    return when {
        this.isEmpty() -> "Необходимов ввести код подтверждения"
        this.length < 6 -> "Необходимо ввести 6 цифр"
        else -> ""
    }
}
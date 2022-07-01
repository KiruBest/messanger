package com.example.messanger.data.repository

import com.example.messanger.data.core.PhoneAuthResult

interface IAccountSource {
    suspend fun performPhoneAuth(phoneNumber: String, password: String): PhoneAuthResult
}
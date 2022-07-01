package com.example.messanger.data.core

import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

sealed class PhoneAuthResult {
    data class VerificationCompleted(val credentials: PhoneAuthCredential) : PhoneAuthResult()
    data class CodeSent(
        val verificationId: String,
        val token: PhoneAuthProvider.ForceResendingToken
    ) : PhoneAuthResult()
    data class Failure(val exception: Exception): PhoneAuthResult()
}

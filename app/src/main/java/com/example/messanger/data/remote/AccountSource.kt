package com.example.messanger.data.remote

import com.example.messanger.data.core.PhoneAuthResult
import com.example.messanger.data.repository.IAccountSource
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AccountSource(
    private val firebaseAuth: FirebaseAuth
) : IAccountSource {
    override suspend fun performPhoneAuth(phoneNumber: String, password: String): PhoneAuthResult =
        suspendCoroutine<PhoneAuthResult> { continuation ->
            val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    continuation.resume(PhoneAuthResult.VerificationCompleted(phoneAuthCredential))
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    continuation.resumeWithException(e)
                    PhoneAuthResult.Failure(e)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verificationId, token)
                    continuation.resume(PhoneAuthResult.CodeSent(verificationId, token))
                }
            }

            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setCallbacks(callback)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }
}
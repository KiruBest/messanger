package com.example.messanger.data.repository

import android.content.ContentValues
import android.util.Log
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.repository.IAccountService
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AccountService(
    private val firebaseAuth: FirebaseAuth
) : IAccountService {
    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    override suspend fun performPhoneAuth(
        phoneNumber: String
    ): AsyncOperationResult<Boolean> = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    Log.d(ContentValues.TAG, "onVerificationCompleted:$phoneAuthCredential")

                    firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(AsyncOperationResult.Success(task.result.user != null))
                        } else {
                            continuation.resume(AsyncOperationResult.Failure(task.exception!!))
                        }
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.w(ContentValues.TAG, "onVerificationFailed", e)
                    continuation.resume(AsyncOperationResult.Failure(e))
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verificationId, token)

                    storedVerificationId = verificationId
                    resendToken = token
                }
            }

            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callback)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    override suspend fun sentAuthCode(code: String): AsyncOperationResult<Boolean> =
        suspendCoroutine { continuation ->
            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
            firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(AsyncOperationResult.Success(task.result.user != null))
                } else {
                    continuation.resume(AsyncOperationResult.Failure(task.exception!!))
                }
            }
        }
}
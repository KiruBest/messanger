package com.example.messanger.data.repository

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
                    Log.d("onVerificationCompleted", "onVerificationCompleted:$phoneAuthCredential")

                    firebaseAuth.signInWithCredential(phoneAuthCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.i("onVerificationCompletedWithoutCode", task.result.user?.uid.toString())
                                continuation.resume(AsyncOperationResult.Success(task.result.user != null))
                            } else {
                                Log.i("onVerificationFailedWithoutCode", task.exception.toString())
                                continuation.resume(AsyncOperationResult.Failure(task.exception!!))
                            }
                        }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.w("onVerificationFailed", "onVerificationFailed", e)
                    continuation.resume(AsyncOperationResult.Failure(e))
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verificationId, token)

                    Log.d("codeSent", "codeSent:$verificationId")

                    storedVerificationId = verificationId
                    resendToken = token

                    continuation.resume(AsyncOperationResult.Success(true))
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
            val credential = storedVerificationId?.let { PhoneAuthProvider.getCredential(it, code) }
            credential?.let {
                firebaseAuth.signInWithCredential(it).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(AsyncOperationResult.Success(task.result.user != null))
                        Log.i("onVerificationCompletedCodeSent", task.result.user?.uid.toString())
                    } else {
                        continuation.resume(AsyncOperationResult.Failure(task.exception!!))
                        Log.i("onVerificationFailedCodeSent", task.exception?.message.toString())
                    }
                }
            }
        }

    override fun userAuthCheck(): Boolean = firebaseAuth.currentUser != null

    override fun logOut() {
        firebaseAuth.signOut()
    }
}
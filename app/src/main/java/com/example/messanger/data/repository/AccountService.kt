package com.example.messanger.data.repository

import android.util.Log
import com.example.messanger.data.core.mapToUserDto
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.core.DatabaseReadDataException
import com.example.messanger.domain.core.UserState
import com.example.messanger.domain.core.UserUnAuthException
import com.example.messanger.domain.model.UserDto
import com.example.messanger.domain.repository.IAccountService
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val USERS_REF = "Users"
private const val USER_STATUS = "status"

class AccountService(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseReference: DatabaseReference
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
        withContext(Dispatchers.IO) {


            suspendCoroutine { continuation ->
                val credential =
                    storedVerificationId?.let { PhoneAuthProvider.getCredential(it, code) }
                credential?.let {
                    firebaseAuth.signInWithCredential(it).addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            firebaseAuth.currentUser?.let { user ->
                                firebaseReference.child(USERS_REF).child(user.uid).setValue(
                                    mapOf(
                                        "id" to user.uid,
                                        "phone" to user.phoneNumber,
                                        USER_STATUS to UserState.ONLINE
                                    )
                                )
                            }

                            continuation.resume(AsyncOperationResult.Success(task.result.user != null))
                            Log.i(
                                "onVerificationCompletedCodeSent",
                                task.result.user?.uid.toString()
                            )
                        } else {
                            continuation.resume(AsyncOperationResult.Failure(task.exception!!))
                            Log.i(
                                "onVerificationFailedCodeSent",
                                task.exception?.message.toString()
                            )
                        }
                    }
                }
            }
        }

    override fun userAuthCheck(): Boolean = firebaseAuth.currentUser != null

    override suspend fun logOut() {
        firebaseAuth.signOut()
    }

    override suspend fun getCurrentUser(): AsyncOperationResult<UserDto> =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                firebaseAuth.currentUser?.uid?.let { userId ->
                    firebaseReference.child(USERS_REF).child(userId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val userDto = snapshot.mapToUserDto()
                                continuation.resume(AsyncOperationResult.Success(userDto))
                            }

                            override fun onCancelled(error: DatabaseError) {
                                continuation.resume(
                                    AsyncOperationResult.Failure(
                                        DatabaseReadDataException()
                                    )
                                )
                            }
                        })
                } ?: continuation.resume(AsyncOperationResult.Failure(UserUnAuthException()))
            }
        }

    override suspend fun updateUserParams(userDto: UserDto): AsyncOperationResult<Boolean> =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                firebaseAuth.currentUser?.uid?.let { userId ->
                    firebaseReference.child(USERS_REF).child(userId).setValue(userDto)
                } ?: continuation.resume(AsyncOperationResult.Failure(UserUnAuthException()))
            }
        }

    override suspend fun updateUserState(state: UserState): AsyncOperationResult<UserDto> =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                firebaseAuth.currentUser?.uid?.let { userId ->
                    firebaseReference.child(USERS_REF).child(userId).child(USER_STATUS)
                        .setValue(state.state)
                } ?: continuation.resume(AsyncOperationResult.Failure(UserUnAuthException()))
            }
        }
}
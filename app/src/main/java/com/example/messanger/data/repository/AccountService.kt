package com.example.messanger.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.example.messanger.core.enumeration.UserState
import com.example.messanger.core.exception.DatabaseReadDataException
import com.example.messanger.core.exception.UserUnAuthException
import com.example.messanger.core.exception.VerificationFailedException
import com.example.messanger.core.result.OperationResult
import com.example.messanger.data.core.Constants.NOTIFICATION_TOKEN_REF
import com.example.messanger.data.core.Constants.USERS_REF
import com.example.messanger.data.core.Constants.USER_ID
import com.example.messanger.data.core.Constants.USER_PHONE
import com.example.messanger.data.core.Constants.USER_STATUS
import com.example.messanger.data.core.Constants.USER_TEXT_STATUS
import com.example.messanger.data.core.mapToUserDto
import com.example.messanger.data.model.mapToDomain
import com.example.messanger.domain.model.User
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
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AccountService(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseReference: DatabaseReference,
    private val storageReference: StorageReference,
    private val firebaseMessaging: FirebaseMessaging
) : IAccountService {
    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    override suspend fun performPhoneAuth(
        phoneNumber: String
    ): OperationResult<Boolean> = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    Log.d("onVerificationCompleted", "onVerificationCompleted:$phoneAuthCredential")
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.w("onVerificationFailed", "onVerificationFailed", e)
                    continuation.resume(OperationResult.Error(VerificationFailedException("Не удалось пройти верефикацию")))
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    super.onCodeSent(verificationId, token)
                    Log.d("codeSent", "codeSent:$verificationId")

                    storedVerificationId = verificationId
                    resendToken = token

                    continuation.resume(OperationResult.Success(true))
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

    override suspend fun sentAuthCode(code: String): OperationResult<Boolean> =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val credential =
                    storedVerificationId?.let { PhoneAuthProvider.getCredential(it, code) }
                credential?.let {
                    firebaseAuth.signInWithCredential(it).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseAuth.currentUser?.let { user ->
                                val refToUser = firebaseReference.child(USERS_REF).child(user.uid)

                                refToUser.child(USER_ID).setValue(user.uid)
                                refToUser.child(USER_PHONE).setValue(user.phoneNumber)
                                refToUser.child(USER_STATUS).setValue(UserState.ONLINE.state)

                                firebaseMessaging.token.addOnCompleteListener { tokenTask ->
                                    if (tokenTask.isSuccessful) {
                                        val token = tokenTask.result
                                        firebaseReference.child(NOTIFICATION_TOKEN_REF)
                                            .child(user.uid).setValue(token)
                                        Log.i("Token", token)
                                    }

                                }
                            }

                            continuation.resume(OperationResult.Success(task.result.user != null))
                            Log.i(
                                "onVerificationCompletedCodeSent",
                                task.result.user?.uid.toString()
                            )
                        } else {
                            continuation.resume(
                                OperationResult.Error(
                                    VerificationFailedException("Не удалось пройти верификацию")
                                )
                            )
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

    override suspend fun logOut(): OperationResult<Boolean> = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            firebaseAuth.currentUser?.uid?.let {
                firebaseReference.child(USERS_REF).child(it).child(USER_STATUS)
                    .setValue(UserState.OFFLINE.state).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseAuth.signOut()
                            continuation.resume(OperationResult.Success(firebaseAuth.currentUser == null))
                        } else {
                            continuation.resume(
                                OperationResult.Error(
                                    DatabaseReadDataException()
                                )
                            )
                        }
                    }
            }
        }
    }

    override suspend fun setUserAccountStatus(text: String): OperationResult<String> =
        withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                firebaseAuth.currentUser?.uid?.let {
                    firebaseReference.child(USERS_REF).child(it)
                        .updateChildren(mapOf(USER_TEXT_STATUS to text))
                } ?: continuation.resume(OperationResult.Error(UserUnAuthException()))
            }
        }

    override suspend fun getCurrentUser(): OperationResult<User> =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                firebaseAuth.currentUser?.uid?.let { userId ->
                    firebaseReference.child(USERS_REF).child(userId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val userDto = snapshot.mapToUserDto()
                                continuation.resume(OperationResult.Success(userDto.mapToDomain()))
                            }

                            override fun onCancelled(error: DatabaseError) {
                                continuation.resume(
                                    OperationResult.Error(
                                        DatabaseReadDataException()
                                    )
                                )
                            }
                        })
                } ?: continuation.resume(OperationResult.Error(UserUnAuthException()))
            }
        }

    override suspend fun updateUserParams(
        userDto: User,
        bitmap: Bitmap?
    ): OperationResult<Boolean> =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                firebaseAuth.currentUser?.uid?.let { userId ->
                    bitmap?.let { bmp ->
                        val ref = storageReference.child("avatars/${userDto.id}.jpg")

                        val baos = ByteArrayOutputStream()
                        bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos)
                        val data = baos.toByteArray()

                        val uploadTask = ref.putBytes(data)

                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                task.exception?.let {
                                    continuation.resume(OperationResult.Error(it))
                                }
                            }
                            ref.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUri = task.result
                                firebaseReference.child(USERS_REF).child(userId)
                                    .setValue(userDto.copy(avatarUrl = downloadUri.toString()))
                            } else {
                                continuation.resume(OperationResult.Error(task.exception!!))
                            }
                        }
                    }

                    firebaseReference.child(USERS_REF).child(userId).setValue(userDto)
                    Log.d("Adin", userDto.toString())
                } ?: continuation.resume(OperationResult.Error(UserUnAuthException()))
            }
        }

    override suspend fun updateUserState(state: UserState): OperationResult<User> =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                firebaseAuth.currentUser?.uid?.let { userId ->
                    firebaseReference.child(USERS_REF).child(userId).child(USER_STATUS)
                        .setValue(state.state)
                } ?: continuation.resume(OperationResult.Error(UserUnAuthException()))
            }
        }
}
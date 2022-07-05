package com.example.messanger.data.repository

import com.example.messanger.data.core.Constants.MESSAGE_REF
import com.example.messanger.data.core.Constants.USERS_REF
import com.example.messanger.data.core.Constants.USER_ID
import com.example.messanger.data.core.mapToUserDto
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.core.DatabaseReadDataException
import com.example.messanger.domain.core.UserUnAuthException
import com.example.messanger.domain.model.MessageDto
import com.example.messanger.domain.model.UserDto
import com.example.messanger.domain.repository.IMessengerService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MessengerService(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseRef: DatabaseReference
) : IMessengerService {
    private val usersList: MutableList<UserDto> = mutableListOf()

    override suspend fun getUsersList(): AsyncOperationResult<List<UserDto>> =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                if (firebaseAuth.currentUser != null) {
                    firebaseRef.child(USERS_REF)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val users: List<UserDto> = snapshot.children.filter {
                                    it.child(USER_ID)
                                        .getValue<String>() != firebaseAuth.currentUser?.uid
                                }.map {
                                    it.mapToUserDto()
                                }
                                usersList.addAll(users)
                                continuation.resume(AsyncOperationResult.Success(users))
                            }

                            override fun onCancelled(error: DatabaseError) {
                                continuation.resume(
                                    AsyncOperationResult.Failure(
                                        DatabaseReadDataException()
                                    )
                                )
                            }
                        })
                } else {
                    continuation.resume(AsyncOperationResult.Failure(UserUnAuthException()))
                }
            }
        }

    override suspend fun sendMessage(text: String, companionID: String): AsyncOperationResult<List<MessageDto>> = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            firebaseAuth.currentUser?.uid?.let { uid ->
                val refDialogCurrentUser = "$MESSAGE_REF/${firebaseAuth.currentUser?.uid}/$companionID"
                val refDialogCompanionUser = "$MESSAGE_REF/$companionID/${firebaseAuth.currentUser?.uid}"
                val messageKey = firebaseRef.child(refDialogCurrentUser).push().key.toString()

                val messageDto = MessageDto(
                    id = messageKey,
                    text = text,
                    type = "Текст",
                    from = uid,
                    timestamp = ServerValue.TIMESTAMP
                )

                val mapDialog = hashMapOf<String, Any>(
                    "$refDialogCurrentUser/$messageKey" to messageDto,
                    "$refDialogCompanionUser/$messageKey" to messageDto
                )

                firebaseRef.updateChildren(mapDialog).addOnCompleteListener {
                    continuation.resume(AsyncOperationResult.Success(emptyList()))
                }.addOnFailureListener {
                    continuation.resume(AsyncOperationResult.Failure(it))
                }
            }
        }
    }

    override suspend fun getMessages(): AsyncOperationResult<List<MessageDto>> {
        TODO("Not yet implemented")
    }

    override fun searchUser(newText: String?): List<UserDto> {
        return if (newText != null) {
            usersList.filter {
                it.fName.contains(newText, true)
                        || it.lName.contains(newText, true)
                        || it.phone.contains(newText, true)
            }
        } else usersList
    }
}
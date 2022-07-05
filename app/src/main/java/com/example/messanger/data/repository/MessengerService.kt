package com.example.messanger.data.repository

import com.example.messanger.data.core.Constants.USERS_REF
import com.example.messanger.data.core.Constants.USER_ID
import com.example.messanger.data.core.mapToUserDto
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.core.DatabaseReadDataException
import com.example.messanger.domain.core.UserUnAuthException
import com.example.messanger.domain.model.UserDto
import com.example.messanger.domain.repository.IMessengerService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
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
package com.example.messanger.data.repository

import android.util.Log
import com.example.messanger.data.core.Constants.CHAT_TYPE
import com.example.messanger.data.core.Constants.MAIN_LIST_REF
import com.example.messanger.data.core.Constants.MESSAGE_FROM
import com.example.messanger.data.core.Constants.MESSAGE_ID
import com.example.messanger.data.core.Constants.MESSAGE_REF
import com.example.messanger.data.core.Constants.MESSAGE_TEXT
import com.example.messanger.data.core.Constants.MESSAGE_TIMESTAMP
import com.example.messanger.data.core.Constants.MESSAGE_TYPE
import com.example.messanger.data.core.Constants.USERS_REF
import com.example.messanger.data.core.Constants.USER_ID
import com.example.messanger.data.core.mapToChatDto
import com.example.messanger.data.core.mapToChatItemDto
import com.example.messanger.data.core.mapToMessageDto
import com.example.messanger.data.core.mapToUserDto
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.core.DatabaseReadDataException
import com.example.messanger.domain.core.UserUnAuthException
import com.example.messanger.domain.model.ChatDto
import com.example.messanger.domain.model.ChatItemDto
import com.example.messanger.domain.model.MessageDto
import com.example.messanger.domain.model.UserDto
import com.example.messanger.domain.repository.IMessengerService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MessengerService(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseRef: DatabaseReference
) : IMessengerService {
    private val usersList: MutableList<UserDto> = mutableListOf()
    private var chatItemList = mutableListOf<ChatItemDto>()

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

    override suspend fun sendMessage(
        text: String,
        companionID: String
    ): AsyncOperationResult<List<MessageDto>> = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            firebaseAuth.currentUser?.uid?.let { uid ->
                val refDialogCurrentUser =
                    "$MESSAGE_REF/${firebaseAuth.currentUser?.uid}/$companionID"
                val refDialogCompanionUser =
                    "$MESSAGE_REF/$companionID/${firebaseAuth.currentUser?.uid}"
                val messageKey = firebaseRef.child(refDialogCurrentUser).push().key.toString()

                val message = mapOf(
                    MESSAGE_ID to messageKey,
                    MESSAGE_TEXT to text,
                    MESSAGE_TYPE to "Текст",
                    MESSAGE_FROM to uid,
                    MESSAGE_TIMESTAMP to ServerValue.TIMESTAMP
                )

                val mapDialog = hashMapOf<String, Any>(
                    "$refDialogCurrentUser/$messageKey" to message,
                    "$refDialogCompanionUser/$messageKey" to message
                )

                firebaseRef.updateChildren(mapDialog).addOnFailureListener {
                    continuation.resume(AsyncOperationResult.Failure(it))
                }
            }
        }
    }

    override suspend fun getMessagesByCompanionId(companionID: String): Flow<AsyncOperationResult<List<MessageDto>>> =
        withContext(Dispatchers.IO) {
            callbackFlow {
                firebaseAuth.currentUser?.uid?.let { uid ->
                    val ref = firebaseRef.child(MESSAGE_REF)
                        .child(uid)
                        .child(companionID)

                    val callback = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val messagesList = snapshot.children.map { it.mapToMessageDto() }
                            if (isActive) trySendBlocking(AsyncOperationResult.Success(messagesList))
                        }

                        override fun onCancelled(error: DatabaseError) {
                            trySendBlocking(AsyncOperationResult.Failure(DatabaseReadDataException()))
                        }
                    }

                    ref.addValueEventListener(callback)

                    awaitClose {
                        ref.removeEventListener(callback)
                    }
                }
            }
        }

    override suspend fun getExistsChats(): Flow<AsyncOperationResult<List<ChatItemDto>>> =
        withContext(Dispatchers.IO) {
            callbackFlow {
                var chatList: List<ChatDto>

                firebaseAuth.currentUser?.uid?.let { uid ->
                    if (chatItemList.isNotEmpty()) trySendBlocking(AsyncOperationResult.Success(chatItemList))

                    val callbackLastMessage = object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try {
                                val message = snapshot.children.map { it.mapToChatItemDto() }[0]
                                val chatItemDto = chatItemList.find { it.userID == snapshot.key }

                                chatItemDto?.text = message.text
                                chatItemDto?.messageID = message.messageID
                                chatItemDto?.from = message.from
                                chatItemDto?.type = message.type
                                chatItemDto?.timestamp = message.timestamp

                                trySendBlocking(
                                    AsyncOperationResult.Success(
                                        ArrayList(chatItemList).toList()
                                    )
                                )
                            } catch (e: Exception) {
                                trySendBlocking(
                                    AsyncOperationResult.Failure(e)
                                )
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            trySendBlocking(AsyncOperationResult.Failure(DatabaseReadDataException()))
                        }
                    }

                    val callbackCompanion = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val chatItemDto = snapshot.mapToChatItemDto()

                            if (!chatItemList.any { it.userID == chatItemDto.userID }) {
                                chatItemList.add(chatItemDto)
                            } else {
                                val item = chatItemList.find { it.userID == chatItemDto.userID }

                                item?.status = chatItemDto.status
                                item?.username = chatItemDto.username
                                item?.fName = chatItemDto.fName
                                item?.lName = chatItemDto.lName
                                item?.avatarUrl = chatItemDto.avatarUrl
                                item?.phone = chatItemDto.phone
                            }

                            trySendBlocking(
                                AsyncOperationResult.Success(
                                    ArrayList(chatItemList).toList()
                                )
                            )

                            val refLastMessage =
                                firebaseRef.child(MESSAGE_REF).child(uid)
                                    .child(chatItemDto.userID).limitToLast(1)

                            refLastMessage.addValueEventListener(callbackLastMessage)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            trySendBlocking(AsyncOperationResult.Failure(DatabaseReadDataException()))
                        }
                    }

                    val callbackChatList = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            chatList = snapshot.children.map { it.mapToChatDto() }

                            chatList.forEach { chat ->
                                val refCompanion = firebaseRef.child(USERS_REF).child(chat.id)
                                refCompanion.addValueEventListener(callbackCompanion)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            trySendBlocking(AsyncOperationResult.Failure(DatabaseReadDataException()))
                        }
                    }

                    val refMainList = firebaseRef.child(MAIN_LIST_REF).child(uid)

                    refMainList.addValueEventListener(callbackChatList)

                    awaitClose {
                        refMainList.removeEventListener(callbackChatList)
                    }

                } ?: trySendBlocking(AsyncOperationResult.Failure(UserUnAuthException()))
            }
        }

    override suspend fun addChat(
        companionID: String,
        chatType: String
    ): AsyncOperationResult<Boolean> = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            firebaseAuth.currentUser?.uid?.let { uid ->
                val refCurrentUser = "$MAIN_LIST_REF/$uid/$companionID"
                val refCompanion = "$MAIN_LIST_REF/$companionID/$uid"

                val mapCurrentUser = hashMapOf<String, Any>(
                    USER_ID to companionID,
                    CHAT_TYPE to chatType
                )

                val mapCompanion = hashMapOf<String, Any>(
                    USER_ID to uid,
                    CHAT_TYPE to chatType
                )

                val commonMap = hashMapOf<String, Any>(
                    refCurrentUser to mapCurrentUser,
                    refCompanion to mapCompanion
                )

                firebaseRef.updateChildren(commonMap)
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
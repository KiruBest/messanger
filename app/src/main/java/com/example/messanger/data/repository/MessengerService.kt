package com.example.messanger.data.repository

import android.util.Log
import com.example.messanger.BuildConfig
import com.example.messanger.data.core.Constants.CHAT_TYPE
import com.example.messanger.data.core.Constants.MAIN_LIST_REF
import com.example.messanger.data.core.Constants.MESSAGE_FROM
import com.example.messanger.data.core.Constants.MESSAGE_ID
import com.example.messanger.data.core.Constants.MESSAGE_REF
import com.example.messanger.data.core.Constants.MESSAGE_SEEN
import com.example.messanger.data.core.Constants.MESSAGE_TEXT
import com.example.messanger.data.core.Constants.MESSAGE_TIMESTAMP
import com.example.messanger.data.core.Constants.MESSAGE_TYPE
import com.example.messanger.data.core.Constants.NOTIFICATION_TOKEN_REF
import com.example.messanger.data.core.Constants.USERS_REF
import com.example.messanger.data.core.Constants.USER_ID
import com.example.messanger.data.core.mapToChatDto
import com.example.messanger.data.core.mapToChatItemDto
import com.example.messanger.data.core.mapToMessageDto
import com.example.messanger.data.core.mapToUserDto
import com.example.messanger.domain.core.AsyncOperationResult
import com.example.messanger.domain.core.DatabaseReadDataException
import com.example.messanger.domain.core.UserUnAuthException
import com.example.messanger.domain.model.*
import com.example.messanger.domain.repository.IMessengerService
import com.example.messanger.presentation.core.CompanionTitleBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MessengerService(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseRef: DatabaseReference
) : IMessengerService {
    private val usersList: MutableList<UserDto> = mutableListOf()
    private val chatItemList = mutableListOf<ChatItemDto>()

    private val gson = Gson()

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
                    MESSAGE_TIMESTAMP to ServerValue.TIMESTAMP,
                    MESSAGE_SEEN to false
                )

                val mapDialog = hashMapOf<String, Any>(
                    "$refDialogCurrentUser/$messageKey" to message,
                    "$refDialogCompanionUser/$messageKey" to message
                )

                firebaseRef.updateChildren(mapDialog).addOnFailureListener {
                    continuation.resume(AsyncOperationResult.Failure(it))
                }

                firebaseRef.child(NOTIFICATION_TOKEN_REF).child(companionID).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val token = snapshot.getValue<String>()

                        firebaseRef.child(USERS_REF).child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val user = snapshot.mapToUserDto()

                                val userName = if (user.fName != "" || user.lName != "") {
                                    "${user.fName} ${user.lName}"
                                } else {
                                    user.phone
                                }

                                CoroutineScope(Dispatchers.IO).launch {
                                    token?.let {
                                        val notificationDto = NotificationDto(
                                            to = it,
                                            notificationNotificationDataDto = NotificationDataDto(
                                                body = text,
                                                title = userName,
                                                companionID = uid,
                                                photo = user.avatarUrl
                                            )
                                        )

                                        val json = gson.toJson(notificationDto)

                                        val body: RequestBody = RequestBody.create(JSON, json)

                                        val request = Request.Builder()
                                            .url(NOTIFICATION_SENDER_URL)
                                            .addHeader("Authorization", "key=$NOTIFICATION_API_KEY")
                                            .addHeader("Content-Type", "application/json")
                                            .post(body)
                                            .build()

                                        val client = OkHttpClient()
                                        val call: Call = client.newCall(request)
                                        call.execute()
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
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
                firebaseAuth.currentUser?.uid?.let { uid ->
                    if (chatItemList.isNotEmpty()) trySendBlocking(
                        AsyncOperationResult.Success(
                            chatItemList
                        )
                    )

                    val callbackLastMessage = object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try {
                                val chatItemDto = chatItemList.find { it.userID == snapshot.key }
                                val message: ChatItemDto = snapshot.children.map { it.mapToChatItemDto() }.last()
                                val messages = snapshot.children.map { it.mapToChatItemDto() }

                                chatItemDto?.text = message.text
                                chatItemDto?.messageID = message.messageID
                                chatItemDto?.from = message.from
                                chatItemDto?.type = message.type
                                chatItemDto?.timestamp = message.timestamp
                                chatItemDto?.seen = message.seen
                                chatItemDto?.noSeenMessageCount = messages.count { !it.seen }

                                trySendBlocking(AsyncOperationResult.Success(ArrayList(chatItemList)))
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
                            val chatItemDto: ChatItemDto = snapshot.mapToChatItemDto()

                            if (!chatItemList.any { it.userID == chatItemDto.userID }) {
                                chatItemList.add(chatItemDto)
                            }

                            chatItemList.find { it.userID == chatItemDto.userID }?.let { item ->
                                item.avatarUrl = chatItemDto.avatarUrl
                                item.fName = chatItemDto.fName
                                item.lName = chatItemDto.lName
                                item.username = chatItemDto.username
                                item.status = chatItemDto.status
                                item.phone = chatItemDto.phone

                                trySendBlocking(AsyncOperationResult.Success(ArrayList(chatItemList)))
                            }

                            val refLastMessage =
                                firebaseRef.child(MESSAGE_REF).child(uid)
                                    .child(chatItemDto.userID)

                            refLastMessage.addValueEventListener(callbackLastMessage)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            trySendBlocking(AsyncOperationResult.Failure(DatabaseReadDataException()))
                        }
                    }

                    val callbackChatList = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val chatList: List<ChatDto> = snapshot.children.map { it.mapToChatDto() }

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

    override suspend fun readMessage(companionID: String, messageID: String) {
        withContext(Dispatchers.IO) {
            firebaseAuth.currentUser?.uid?.let { userID ->
                firebaseRef.child(MESSAGE_REF).child(userID).child(companionID).child(messageID)
                    .updateChildren(mapOf(MESSAGE_SEEN to true))
            }
        }
    }

    override suspend fun getCompanionById(companionID: String): AsyncOperationResult<UserDto> = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            firebaseRef.child(USERS_REF).child(companionID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.mapToUserDto()

                    continuation.resume(AsyncOperationResult.Success(user))
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resume(AsyncOperationResult.Failure(DatabaseReadDataException()))
                }
            })
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

    companion object {
        private val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        private const val NOTIFICATION_SENDER_URL = "https://fcm.googleapis.com/fcm/send"
        private const val NOTIFICATION_API_KEY = BuildConfig.NOTIFICATION_API_KEY
    }
}
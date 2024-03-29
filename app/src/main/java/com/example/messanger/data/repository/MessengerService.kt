package com.example.messanger.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.example.messanger.BuildConfig
import com.example.messanger.core.enumeration.MessageType
import com.example.messanger.core.exception.DatabaseReadDataException
import com.example.messanger.core.exception.UserUnAuthException
import com.example.messanger.core.result.FlowListResult
import com.example.messanger.core.result.ListResult
import com.example.messanger.core.result.OperationResult
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
import com.example.messanger.data.model.*
import com.example.messanger.presentation.model.MessageUi
import com.example.messanger.presentation.model.UserUi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MessengerService(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseRef: DatabaseReference,
    private val firebaseReference: DatabaseReference,
    private val gson: Gson,
    private val storageReference: StorageReference
) : IMessengerService {
    private val usersList = MutableStateFlow<List<UserDto>>(emptyList())
    private val chatItemList = MutableStateFlow<List<ChatItemDto>>(emptyList())

    override suspend fun getUsersList(): ListResult<UserUi> =
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
                                usersList.value = users
                                continuation.resume(OperationResult.Success(users.map(UserDto::mapToUi)))
                            }

                            override fun onCancelled(error: DatabaseError) {
                                continuation.resume(
                                    OperationResult.Error(
                                        DatabaseReadDataException()
                                    )
                                )
                            }
                        })
                } else {
                    continuation.resume(OperationResult.Error(UserUnAuthException()))
                }
            }
        }

    override suspend fun sendMessage(
        text: String,
        companionID: String,
        messageType: MessageType
    ): ListResult<MessageUi> = withContext(Dispatchers.IO) {
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
                    MESSAGE_TYPE to messageType.type,
                    MESSAGE_FROM to uid,
                    MESSAGE_TIMESTAMP to ServerValue.TIMESTAMP,
                    MESSAGE_SEEN to false
                )

                val mapDialog = hashMapOf<String, Any>(
                    "$refDialogCurrentUser/$messageKey" to message,
                    "$refDialogCompanionUser/$messageKey" to message
                )

                firebaseRef.updateChildren(mapDialog).addOnFailureListener {
                    continuation.resume(OperationResult.Error(it))
                }

                firebaseRef.child(NOTIFICATION_TOKEN_REF).child(companionID)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val token = snapshot.getValue<String>()

                            firebaseRef.child(USERS_REF).child(uid)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
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

                                                val body: RequestBody =
                                                    RequestBody.create(JSON, json)

                                                val request = Request.Builder()
                                                    .url(NOTIFICATION_SENDER_URL)
                                                    .addHeader(
                                                        "Authorization",
                                                        "key=$NOTIFICATION_API_KEY"
                                                    )
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

    override suspend fun getMessagesByCompanionId(companionID: String): FlowListResult<MessageUi> =
        withContext(Dispatchers.IO) {
            callbackFlow {
                firebaseAuth.currentUser?.uid?.let { uid ->
                    val ref = firebaseRef.child(MESSAGE_REF)
                        .child(uid)
                        .child(companionID)

                    val callback = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val messagesList = snapshot.children.map { it.mapToMessageDto() }
                            if (isActive) trySendBlocking(
                                OperationResult.Success(
                                    messagesList.map(
                                        MessageDto::mapToUi
                                    )
                                )
                            )
                        }

                        override fun onCancelled(error: DatabaseError) {
                            trySendBlocking(OperationResult.Error(DatabaseReadDataException()))
                        }
                    }

                    ref.addValueEventListener(callback)

                    awaitClose {
                        ref.removeEventListener(callback)
                    }
                }
            }
        }

    override suspend fun getExistsChats(): FlowListResult<ChatItemDto> =
        withContext(Dispatchers.IO) {
            callbackFlow {
                firebaseAuth.currentUser?.uid?.let { uid ->
                    val copiedChatItemList = chatItemList.value.toMutableList()

                    if (copiedChatItemList.isNotEmpty()) {
                        trySendBlocking(OperationResult.Success(copiedChatItemList))
                    }


                    val callbackLastMessage = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            try {
                                val chatItemDtoId =
                                    copiedChatItemList.indexOfFirst { it.userID == snapshot.key }
                                val chatItemDto = copiedChatItemList[chatItemDtoId]
                                val message: ChatItemDto =
                                    snapshot.children.map { it.mapToChatItemDto() }.last()
                                val messages = snapshot.children.map { it.mapToChatItemDto() }

                                copiedChatItemList[chatItemDtoId] = chatItemDto.copy(
                                    text = message.text,
                                    messageID = message.messageID,
                                    from = message.from,
                                    type = message.type,
                                    timestamp = message.timestamp,
                                    seen = message.seen,
                                    noSeenMessageCount = messages.count { !it.seen }
                                )

                                chatItemList.value = copiedChatItemList

                                trySendBlocking(OperationResult.Success(copiedChatItemList.toList()))
                            } catch (e: Exception) {
                                trySendBlocking(OperationResult.Error(e))
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            trySendBlocking(OperationResult.Error(DatabaseReadDataException()))
                        }
                    }

                    val callbackCompanion = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val chatItemDto: ChatItemDto = snapshot.mapToChatItemDto()

                            if (!copiedChatItemList.any { it.userID == chatItemDto.userID }) {
                                copiedChatItemList.add(chatItemDto)
                            }

                            val itemId =
                                copiedChatItemList.indexOfFirst { it.userID == chatItemDto.userID }
                            copiedChatItemList[itemId] = copiedChatItemList[itemId].copy(
                                avatarUrl = chatItemDto.avatarUrl,
                                fName = chatItemDto.fName,
                                lName = chatItemDto.lName,
                                username = chatItemDto.username,
                                status = chatItemDto.status,
                                phone = chatItemDto.phone,
                            )

                            chatItemList.value = copiedChatItemList

                            trySendBlocking(OperationResult.Success(ArrayList(copiedChatItemList)))

                            val refLastMessage =
                                firebaseRef.child(MESSAGE_REF).child(uid)
                                    .child(chatItemDto.userID)

                            refLastMessage.addValueEventListener(callbackLastMessage)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            trySendBlocking(OperationResult.Error(DatabaseReadDataException()))
                        }
                    }

                    val callbackChatList = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val chatList: List<ChatDto> =
                                snapshot.children.map { it.mapToChatDto() }

                            chatList.forEach { chat ->
                                val refCompanion = firebaseRef.child(USERS_REF).child(chat.id)

                                refCompanion.addValueEventListener(callbackCompanion)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            trySendBlocking(OperationResult.Error(DatabaseReadDataException()))
                        }
                    }

                    val refMainList = firebaseRef.child(MAIN_LIST_REF).child(uid)

                    refMainList.addValueEventListener(callbackChatList)

                    awaitClose {
                        refMainList.removeEventListener(callbackChatList)
                    }
                } ?: trySendBlocking(OperationResult.Error(UserUnAuthException()))
            }
        }

    override suspend fun addChat(
        companionID: String,
        chatType: String
    ): OperationResult<Boolean> = withContext(Dispatchers.IO) {
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

    override suspend fun readMessage(
        companionID: String,
        messageID: String
    ): OperationResult<Unit> {
        return withContext(Dispatchers.IO) {
            firebaseAuth.currentUser?.uid?.let { userID ->
                firebaseRef.child(MESSAGE_REF).child(userID).child(companionID).child(messageID)
                    .updateChildren(mapOf(MESSAGE_SEEN to true))
                //todo [Backlog] Дичь, вспомнить че к чему, сделать норм
                OperationResult.Success(Unit)
            } ?: OperationResult.Error(DatabaseReadDataException())
        }
    }

    override suspend fun getCompanionById(companionID: String): OperationResult<UserUi> =
        withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                firebaseRef.child(USERS_REF).child(companionID)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.mapToUserDto()

                            continuation.resume(OperationResult.Success(user.mapToUi()))
                        }

                        override fun onCancelled(error: DatabaseError) {
                            continuation.resume(OperationResult.Error(DatabaseReadDataException()))
                        }
                    })
            }
        }

    override suspend fun searchUser(newText: String?): ListResult<UserUi> {
        return withContext(Dispatchers.IO) {
            val copiedUsersList = usersList.value
            if (newText != null) {
                val filteredList = copiedUsersList.filter {
                    it.fName.contains(newText, true)
                            || it.lName.contains(newText, true)
                            || it.phone.contains(newText, true)
                }.map(UserDto::mapToUi)
                if (filteredList.isNotEmpty()) {
                    OperationResult.Success(filteredList)
                } else {
                    OperationResult.Empty
                }
            } else {
                OperationResult.Success(copiedUsersList.map(UserDto::mapToUi))
            }
        }
    }

    override suspend fun sendPicture(bitmap: Bitmap, companionID: String): OperationResult<String> {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val ref = storageReference.child("files/${companionID + bitmap.hashCode()}.jpg")

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
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
                        val downloadUri = task.result.toString()
                        continuation.resume(OperationResult.Success(downloadUri))
                    } else {
                        continuation.resume(OperationResult.Error(task.exception!!))
                    }
                }
            }
        }
    }


    companion object {
        private val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        private const val NOTIFICATION_SENDER_URL = "https://fcm.googleapis.com/fcm/send"
        private const val NOTIFICATION_API_KEY = BuildConfig.NOTIFICATION_API_KEY
    }
}

interface IMessengerService {
    suspend fun getUsersList(): ListResult<UserUi>
    suspend fun sendMessage(
        text: String,
        companionID: String,
        messageType: MessageType = MessageType.TEXT
    ): ListResult<MessageUi>

    suspend fun getMessagesByCompanionId(companionID: String): FlowListResult<MessageUi>
    suspend fun getExistsChats(): FlowListResult<ChatItemDto>
    suspend fun addChat(companionID: String, chatType: String): OperationResult<Boolean>
    suspend fun readMessage(companionID: String, messageID: String): OperationResult<Unit>
    suspend fun getCompanionById(companionID: String): OperationResult<UserUi>
    suspend fun searchUser(newText: String?): ListResult<UserUi>
    suspend fun sendPicture(bitmap: Bitmap, companionID: String): OperationResult<String>
}
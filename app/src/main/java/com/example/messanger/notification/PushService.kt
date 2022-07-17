package com.example.messanger.notification

import android.content.Intent
import android.util.Log
import com.example.messanger.presentation.core.Constants.BODY
import com.example.messanger.presentation.core.Constants.COMPANION_ID
import com.example.messanger.presentation.core.Constants.PHOTO
import com.example.messanger.presentation.core.Constants.TITLE
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage

class PushService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        message.data.entries.forEach { entry ->
            Log.v("TAG1", "key = ${entry.key}\tvalue = ${entry.value}")
        }

        val intent = Intent(PUSH_INTENT_FILTER)

        intent.putExtra(TITLE, message.data[TITLE])
        intent.putExtra(BODY, message.data[BODY])
        intent.putExtra(COMPANION_ID, message.data[COMPANION_ID])
        intent.putExtra(PHOTO, message.data[PHOTO])

        sendBroadcast(intent)
    }

    companion object {
        const val PUSH_INTENT_FILTER = "PUSH_EVENT"
    }
}
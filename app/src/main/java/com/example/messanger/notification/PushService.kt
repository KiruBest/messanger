package com.example.messanger.notification

import android.content.Intent
import android.util.Log
import com.example.messanger.presentation.core.Constants.COMPANION_ID
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

        intent.putExtra("title", message.data["title"])
        intent.putExtra("body", message.data["body"])
        intent.putExtra(COMPANION_ID, message.data["companion_id"])

        sendBroadcast(intent)
    }

    companion object {
        const val PUSH_INTENT_FILTER = "PUSH_EVENT"
    }
}
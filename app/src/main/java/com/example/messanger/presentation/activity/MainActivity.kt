package com.example.messanger.presentation.activity

import android.app.*
import android.content.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.messanger.R
import com.example.messanger.notification.PushService
import com.example.messanger.presentation.core.Constants.COMPANION_ID

class MainActivity : AppCompatActivity() {

    private lateinit var pushBroadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pushBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val extras = intent?.extras
                val title = extras?.getString("title")
                val body = extras?.getString("body")

                Log.i("TAG1", extras.toString())

                val resultIntent = Intent(context, MainActivity::class.java)

                resultIntent.putExtra(NOTIFICATION_EXTRAS, extras)

                val resultPendingIntent = PendingIntent.getActivity(
                    context, 0, resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val notification = Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.ic_gazprom)
                    .setContentIntent(resultPendingIntent)
                    .build()

                val name = NOTIFICATION_CHANNEL_ID
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)

                // Register the channel with the system
                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)

                with(NotificationManagerCompat.from(context!!)) {
                    notify(0, notification)
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(PushService.PUSH_INTENT_FILTER)

        registerReceiver(pushBroadcastReceiver, intentFilter)
    }

    override fun onStart() {
        super.onStart()

        intent.extras?.getBundle(NOTIFICATION_EXTRAS)?.let { bundle ->
            Log.i("TAG1", bundle.getString(COMPANION_ID).toString())
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.chatFragment, bundleOf(COMPANION_ID to bundle.getString(COMPANION_ID)))
        }
    }

    override fun onDestroy() {
        unregisterReceiver(pushBroadcastReceiver)
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "gazprom_notification_channel"
        private const val NOTIFICATION_EXTRAS = "notificationExtras"
    }
}
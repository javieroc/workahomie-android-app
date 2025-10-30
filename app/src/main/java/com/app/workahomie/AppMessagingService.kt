package com.app.workahomie

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.URL
import kotlin.concurrent.thread

class AppMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("fcm_token", token)
            .apply()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "From: ${remoteMessage.from}")

        // Handle notification payload (from Firebase Console)
        remoteMessage.notification?.let { notif ->
            sendNotification(notif.title, notif.body, null, null)
        }

        // Handle data payload (custom payload with image and name)
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM", "Message data payload: ${remoteMessage.data}")

            val title = remoteMessage.data["title"]
            val body = remoteMessage.data["body"]
            val name = remoteMessage.data["name"]
            val imageUrl = remoteMessage.data["imageUrl"]

            thread {
                val bitmap = try {
                    imageUrl?.let { URL(it).openStream() }?.use { BitmapFactory.decodeStream(it) }
                } catch (e: Exception) {
                    null
                }
                sendNotification(title, body, name, bitmap)
            }
        }
    }

    private fun sendNotification(
        title: String?,
        body: String?,
        name: String?,
        imageBitmap: android.graphics.Bitmap?
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            // Don't clear task here; just reuse top activity if alive
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("screen", "requests")
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0, // keep same requestCode
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "requests_channel"

        // Create notification channel (Android 8.0+)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Request Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title ?: "Nueva solicitud")
            .setContentText(if (name != null && body != null) "$name $body" else body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Show image if available
        imageBitmap?.let {
            notificationBuilder.setLargeIcon(it)
            notificationBuilder.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(it)
                    .bigLargeIcon(null as android.graphics.Bitmap?)
            )
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}

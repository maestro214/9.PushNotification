package com.example.pushnotification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pushnotification.NotificationType.*
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remotemessage: RemoteMessage) {
        super.onMessageReceived(remotemessage)

        createNotificationChannel()

        val type = remotemessage.data["type"]
            ?.let { valueOf(it) }
        val title = remotemessage.data["title"]
        val message = remotemessage.data["message"]

        type ?: return

        NotificationManagerCompat.from(this)
            .notify(type.id, createeNotification(type, title, message))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.description = CHANNEL_DESCRIPTION

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createeNotification(
        type: NotificationType,
        title: String?,
        message: String?
    ): Notification {
        val intent = Intent(this,MainActivity::class.java).apply {
            putExtra("notificationType", "${type.title} 타입")
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            type.id,
            intent,
            FLAG_UPDATE_CURRENT
        )



        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        when (type) {
            NORMAL -> Unit
            EXPANDABLE -> {
                notificationBuilder.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "😀 😃 😄 😁 😆 😅 😂 🤣 🥲 ☺️ 😊 😇 " +
                                    "🙂 🙃 😉 😌 😍 🥰 😘 😗 😙 😚 😋 😛 " +
                                    "😝 😜 🤪 🤨 🧐 🤓 😎 🥸 🤩 🥳 😏 😒 " +
                                    "😞 😔 😟 😕 🙁 ☹️ 😣 😖 😫 😩 🥺 😢 " +
                                    "😭 😤 😠 😡 🤬 🤯 😳 🥵 🥶 😱 😨 😰 " +
                                    "😥 😓 🤗 🤔 🤭 🤫 🤥 😶 😐 😑 😬 🙄 " +
                                    "😯 😦 😧 😮 😲 🥱 😴 🤤 😪 😵 🤐 🥴 " +
                                    "🤢 🤮 🤧 😷 🤒 🤕"
                        )
                )
            }
             CUSTOM -> {
                notificationBuilder
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(
                        RemoteViews(
                            packageName,
                            R.layout.view_custom_notification
                        ).apply {
                            setTextViewText(R.id.notifiTitle, title)
                            setTextViewText(R.id.notifiMessage, message)
                        }
                    )


            }
        }
        return notificationBuilder.build()
    }

    companion object {
        private const val CHANNEL_NAME = "Emoji Party"
        private const val CHANNEL_DESCRIPTION = "Emoji Party를 위한 채널"
        private const val CHANNEL_ID = "Channel id"
    }

}
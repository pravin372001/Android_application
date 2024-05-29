package com.example.workmanager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

object NotificationUtils {
    const val CHANNEL_ID = "ImageCompressionChannel"
    const val NOTIFICATION_ID = 1

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Image Compression"
            val descriptionText = "Notifications for image compression task"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("NotificationUtils", "Notification channel created")
        }
    }

    fun createNotification(context: Context): Notification {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Image Compression")
            .setContentText("Compressing your image...")
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setOngoing(true)
            .build()
        Log.d("NotificationUtils", "Notification created")
        return notification
    }

    fun updateNotification(context: Context) {
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
        Log.d("NotificationUtils", "Notification updated")
    }
}


package com.pravin.tripwake

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp class TripWakeHilt : Application(){
    override fun onCreate() {
        super.onCreate()
        val notificationChannel = NotificationChannel(
            "location_alert_channel",
            "Location Alert",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NotificationManager::class.java) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

}
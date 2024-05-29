// File: app/src/main/java/com/example/serviceexample/TimerService.kt

package com.example.serviceexample

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class TimerService : Service() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var timeInSeconds = 0
    private var isRunning = false

    companion object {
        const val TIMER_UPDATED = "timerUpdated"
        const val TIME_EXTRA = "timeExtra"
        const val CHANNEL_ID = "TimerServiceChannel"
        const val STOP_TIMER_ACTION = "STOP_TIMER"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == STOP_TIMER_ACTION) {
            stopSelf()
            return START_NOT_STICKY
        }

        if (!isRunning) {
            isRunning = true
            coroutineScope.launch {
                while (isRunning) {
                    delay(1000)
                    timeInSeconds++
                    val time = formatTime(timeInSeconds)
                    Intent(TIMER_UPDATED).apply {
                        putExtra(TIME_EXTRA, time)
                        sendBroadcast(this)
                    }
                    updateNotification(time)
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        coroutineScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun formatTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    private fun updateNotification(time: String) {
        val stopIntent = Intent(this, TimerService::class.java).apply {
            action = STOP_TIMER_ACTION
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Timer Service")
            .setContentText(time)
            .setSmallIcon(R.drawable.timer)
            .addAction(R.drawable.stop_button, "Stop", stopPendingIntent)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
            Log.d("ServiceExample -> TimerService", "Notification channel created")
        }
    }
}

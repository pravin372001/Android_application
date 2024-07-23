package com.pravin.tripwake.model.service.workmanager

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.pravin.tripwake.R
import com.pravin.tripwake.listener.TrackingListenerHolder
import com.pravin.tripwake.model.service.broadcast.StopAlarmReceiver
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class TripWorkManager(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val channelId = "location_alert_channel"
    private var shouldStop = false

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        val destinationLat = inputData.getDouble("destination_lat", 0.0)
        val destinationLon = inputData.getDouble("destination_lon", 0.0)
        val radius = inputData.getFloat("radius", 0f)

        val destinationLocation = Location("").apply {
            latitude = destinationLat
            longitude = destinationLon
        }

        while (!shouldStop) {
            val location = fusedLocationClient.lastLocation.await()
            location?.let {
                if (isWithinRadius(it, destinationLocation, radius)) {
                    showNotification()
                    triggerAlarmAndVibration()
                    shouldStop = true
                    TrackingListenerHolder.listener?.onTrackingChanged(false)
                    return Result.success()
                }
            }
            delay(5000) // Check location every 5 seconds
        }

        return Result.success()
    }

    private fun isWithinRadius(currentLocation: Location, destinationLocation: Location, radius: Float): Boolean {
        val distance = FloatArray(2)
        Location.distanceBetween(
            currentLocation.latitude, currentLocation.longitude,
            destinationLocation.latitude, destinationLocation.longitude,
            distance
        )
        return distance[0] <= radius
    }

    private fun triggerAlarmAndVibration() {
        val vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(0, 1000, 1000) // Vibrate for 1 second, then pause for 1 second

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createWaveform(pattern, 0) // Repeat indefinitely
            vibrator.vibrate(vibrationEffect)
        } else {
            vibrator.vibrate(pattern, 0) // Repeat indefinitely for older versions
        }

        // Trigger alarm sound logic
    }

    private fun stopAlarmAndVibration() {
        val vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.cancel()

        // Stop alarm sound logic
    }

    private fun showNotification() {
        val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
        Log.d("showNotification", "showNotification: called")
        val stopAlarmIntent = Intent(applicationContext, StopAlarmReceiver::class.java).apply {
            action = "STOP_ALARM_ACTION"
        }

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val stopAlarmPendingIntent = PendingIntent.getBroadcast(applicationContext, 0, stopAlarmIntent, flag)

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("Location Alert")
            .setContentText("You have reached your destination radius.")
            .setSmallIcon(R.drawable.ic_clock)
            .addAction(R.drawable.ic_exit, "Stop Alarm", stopAlarmPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Ensure the notification is cleared when the user taps it
            .build()

        notificationManager.notify(1, notification)
    }

}

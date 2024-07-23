package com.pravin.tripwake.model.service.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import androidx.core.app.NotificationManagerCompat
import com.pravin.tripwake.listener.TrackingListenerHolder

class StopAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "STOP_ALARM_ACTION") {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.cancel()
            TrackingListenerHolder.listener?.onTrackingChanged(false)
            // Stop alarm sound logic

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.cancel(1)
        }
    }
}

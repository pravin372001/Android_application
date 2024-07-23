package com.pravin.tripwake.listener

interface TrackingListener {
    fun onTrackingChanged(isTracking: Boolean)
}

object TrackingListenerHolder {
    var listener: TrackingListener? = null
}

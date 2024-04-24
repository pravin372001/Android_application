package com.example.dessertpusher

import android.os.Handler
import android.os.Looper
import android.support.v4.os.IResultReceiver.Default
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import timber.log.Timber

class DessertTimer(lifecycle: Lifecycle): DefaultLifecycleObserver {

    init {
        lifecycle.addObserver(this)
    }

    var secondsCount = 0
    private var handler = Handler(Looper.getMainLooper())

    private lateinit var runnable: Runnable

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        // Create the runnable action
        runnable = object : Runnable {
            override fun run() {
                secondsCount++
                Timber.i("Timer is at : $secondsCount")
                // Post the same runnable again after 1 second (1000ms)
                handler.postDelayed(this, 1000L)
            }
        }

        // Post the runnable to start the timer
        handler.postDelayed(runnable, 1000L)
    }


    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        // Removes all pending posts of runnable from the handler's queue, effectively stopping the
        // timer
        handler.removeCallbacks(runnable)
    }
}
package com.example.serviceexample

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    private val _time = MutableStateFlow("00:00:00")
    val time: StateFlow<String> = _time

    private val timerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getStringExtra(TimerService.TIME_EXTRA)?.let { time ->
                _time.value = time
            }
        }
    }

    init {
        Log.d("TimerViewModel", "Initializing TimerViewModel")
        application.registerReceiver(timerReceiver, IntentFilter(TimerService.TIMER_UPDATED))
    }

    fun startTimer() {
        Log.d("TimerViewModel", "Starting Timer")
        getApplication<Application>().startService(Intent(getApplication(), TimerService::class.java))
    }

    fun stopTimer() {
        Log.d("TimerViewModel", "Stopping Timer")
        getApplication<Application>().stopService(Intent(getApplication(), TimerService::class.java))
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TimerViewModel", "Clearing TimerViewModel")
        getApplication<Application>().unregisterReceiver(timerReceiver)
    }
}

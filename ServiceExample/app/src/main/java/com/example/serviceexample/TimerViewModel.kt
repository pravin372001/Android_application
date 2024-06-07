package com.example.serviceexample

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
        application.registerReceiver(timerReceiver, IntentFilter(TimerService.TIMER_UPDATED))
    }

    fun startTimer() {
        getApplication<Application>().startService(Intent(getApplication(), TimerService::class.java))
    }

    fun stopTimer() {
        getApplication<Application>().stopService(Intent(getApplication(), TimerService::class.java))
    }

    override fun onCleared() {
        super.onCleared()
        getApplication<Application>().unregisterReceiver(timerReceiver)
    }
}

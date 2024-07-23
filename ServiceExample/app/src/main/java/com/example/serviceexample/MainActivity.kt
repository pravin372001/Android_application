package com.example.serviceexample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.serviceexample.ui.theme.ServiceExampleTheme

class MainActivity : ComponentActivity() {
    private val PERMISSION_REQUEST_CODE = 123
    private val timerViewModel: TimerViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "Requesting permissions")
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.FOREGROUND_SERVICE,
                android.Manifest.permission.POST_NOTIFICATIONS
            ),
            PERMISSION_REQUEST_CODE
        )

        setContent {
            ServiceExampleTheme {
                TimerScreen(timerViewModel)
            }
        }
    }
}

@Composable
fun TimerScreen(timerViewModel: TimerViewModel) {
    val time by timerViewModel.time.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = time, style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = {
                Log.d("TimerScreen", "Start button clicked")
                timerViewModel.startTimer()
            }) {
                Text(text = "Start")
            }
            Button(onClick = {
                Log.d("TimerScreen", "Stop button clicked")
                timerViewModel.stopTimer()
            }) {
                Text(text = "Stop")
            }
        }
    }
}

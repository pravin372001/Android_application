package com.pravin.tripwake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.android.libraries.places.api.Places
import com.pravin.tripwake.ui.theme.TripWakeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripWakeTheme {
                Places.initialize(LocalContext.current, stringResource(R.string.MAPS_API_KEY))
                TripWakeApp()
            }
        }
    }
}

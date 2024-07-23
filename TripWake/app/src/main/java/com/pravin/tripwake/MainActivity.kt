package com.pravin.tripwake

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.places.api.Places
import com.pravin.tripwake.listener.ActivityProvider
import com.pravin.tripwake.listener.TrackingListenerHolder
import com.pravin.tripwake.screens.map.MapScreenViewModel
import com.pravin.tripwake.ui.theme.TripWakeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity(), ActivityProvider {
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, proceed with your functionality
            } else {
                // Permission denied, show an appropriate message to the user
            }
        }

        setContent {
            TripWakeTheme {
                Places.initialize(LocalContext.current, stringResource(R.string.MAPS_API_KEY))
                TripWakeApp()
            }
        }
    }

    override fun getActivity(): Activity {
        return this
    }

    fun requestNotificationPermission(onPermissionGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is granted, proceed with your functionality
                    Log.d("onCreateTrip", "requestNotificationPermission: granted")
                    onPermissionGranted()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Show rationale and request permission
                    requestPermissionsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Directly request permission
                    Log.d("onCreateTrip", "requestNotificationPermission: not granted")
                    requestPermissionsLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // No runtime permission required below Android 13
            Log.d("onCreateTrip", "requestNotificationPermission: granted in below Android 13")
            onPermissionGranted()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        TrackingListenerHolder.listener = null
    }
}

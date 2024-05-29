package com.example.mediagallery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import com.example.mediagallery.ui.theme.MediaGalleryTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaGalleryTheme {
                val photos = viewModel.photos.collectAsState().value
                Surface(color = MaterialTheme.colorScheme.background) {
                    PhotoGallery(photos)
                }
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.loadPhotos(contentResolver)
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.loadPhotos(contentResolver)
            }
        }.launch(Manifest.permission.READ_MEDIA_IMAGES)
    }
}

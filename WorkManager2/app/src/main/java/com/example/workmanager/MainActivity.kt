package com.example.workmanager

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import coil.compose.rememberImagePainter

class MainActivity : ComponentActivity() {
    private val PERMISSION_REQUEST_CODE = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationUtils.createNotificationChannel(this)

        // Check and request foreground service permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.FOREGROUND_SERVICE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.FOREGROUND_SERVICE),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted or not needed, continue with app initialization
            setContent {
                ImageCompressApp(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, continue with app initialization
                setContent {
                    ImageCompressApp(intent)
                }
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun ImageCompressApp(intent: Intent) {
    var originalImageUri by remember { mutableStateOf<Uri?>(null) }
    var compressedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    LaunchedEffect(intent) {
        if (intent.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
            val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            if (imageUri != null) {
                originalImageUri = imageUri

                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()

                val inputData = workDataOf("imageUri" to imageUri.toString())

                val workRequest = OneTimeWorkRequestBuilder<ImageCompressWorker>()
                    .setConstraints(constraints)
                    .setInputData(inputData)
                    .build()

                WorkManager.getInstance(context).enqueue(workRequest)

                WorkManager.getInstance(context).getWorkInfoByIdLiveData(workRequest.id).observeForever { workInfo ->
                    if (workInfo != null && workInfo.state.isFinished) {
                        val compressedImageUriString = workInfo.outputData.getString("compressedImageUri")
                        if (compressedImageUriString != null) {
                            compressedImageUri = Uri.parse(compressedImageUriString)
                        }
                    }
                }
            }
        }
    }

    ImageCompressScreen(originalImageUri, compressedImageUri)
}

@Composable
fun ImageCompressScreen(originalImageUri: Uri?, compressedImageUri: Uri?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        originalImageUri?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberImagePainter(it),
                contentDescription = "Original Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        compressedImageUri?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberImagePainter(it),
                contentDescription = "Compressed Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ImageCompressScreen(null, null)
}

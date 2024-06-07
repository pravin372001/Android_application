package com.example.workmanager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ImageCompressWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo())
        return withContext(Dispatchers.IO) {
            try {
                // Get the input image URI from input data
                val imageUriString = inputData.getString("imageUri") ?: return@withContext Result.failure()
                val imageUri = Uri.parse(imageUriString)

                // Get the bitmap from the URI
                val bitmap = BitmapFactory.decodeStream(applicationContext.contentResolver.openInputStream(imageUri))

                // Compress the bitmap
                val compressedFile = File(applicationContext.cacheDir, "compressed_image.jpg")
                val outputStream = FileOutputStream(compressedFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                outputStream.close()

                // Return the URI of the compressed image
                val outputData = workDataOf("compressedImageUri" to Uri.fromFile(compressedFile).toString())
                NotificationUtils.updateNotification(applicationContext)
                Result.success(outputData)
            } catch (e: Exception) {
                Log.e("ImageCompressWorker", "Error compressing image", e)
                Result.failure()
            }
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val notification = NotificationUtils.createNotification(applicationContext)
        Log.d("ImageCompressWorker", "Foreground notification created")
        return ForegroundInfo(NotificationUtils.NOTIFICATION_ID, notification)
    }
}

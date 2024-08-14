package com.pravin.gugan.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.pravin.gugan.database.ImageDatabase
import com.pravin.gugan.database.ImageEntity

class ImageRepository(context: Context) {
    private val imageDao = ImageDatabase.getDatabase(context).imageDao()

    suspend fun insertImage(imageEntity: ImageEntity) {
        imageDao.insertImage(imageEntity)
    }

    suspend fun getAllImages(): LiveData<ImageEntity> {
        return imageDao.getAllImages()
    }

    suspend fun deleteAllImages() {
        imageDao.deleteAllImages()
    }

}
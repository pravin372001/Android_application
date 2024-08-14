package com.pravin.gugan.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImageDao {

    @Insert
    suspend fun insertImage(imageEntity: ImageEntity)

    @Query("SELECT * FROM ImageEntity ORDER BY id DESC LIMIT 1")
    fun getAllImages(): LiveData<ImageEntity>

    @Query("DELETE FROM ImageEntity")
    suspend fun deleteAllImages()

}
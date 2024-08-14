package com.pravin.gugan.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUri: String,
)
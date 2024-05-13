package com.example.roomretrofitrecycler.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gender_table")
data class Gender(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val count: Int,
    val gender: String,
    val name: String,
    val probability: Double
)
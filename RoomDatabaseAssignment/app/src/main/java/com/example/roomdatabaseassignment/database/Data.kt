package com.example.roomdatabaseassignment.database

// DataEntity.kt
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_table")
data class Data(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val age: Int,
    val address: String,
    val phone: String,
    val email: String
)

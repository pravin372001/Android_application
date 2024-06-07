package com.example.jetpacknews.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather")
data class CurrentWeather(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val time: String,
    val temperature: Double,
    val uvIndex: Int,
    val visibility: Double,
    val windDirection: Double,
    val windSpeed: Double,
    val humidity: Int,
    val weatherCode: Int
)

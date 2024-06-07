package com.example.jetpacknews.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WeatherDao {
    @Insert
    suspend fun insertWeather(weather: CurrentWeather)

    @Query("DELETE FROM current_weather")
    suspend fun deleteWeather()

    @Query("SELECT * FROM current_weather ORDER BY id DESC LIMIT 1")
    fun getWeather(): LiveData<CurrentWeather>

}
package com.example.newswithweather.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.newswithweather.database.CurrentWeather
import com.example.newswithweather.database.NewsDatabase
import com.example.newswithweather.database.WeatherDao

class WeatherRepository(contenxt: Context) {
    private val weatherDao: WeatherDao

    init {
        val database = NewsDatabase.getDatabase(contenxt)
        weatherDao = database.weatherDao()
    }

    suspend fun insert(weather: CurrentWeather) {
        weatherDao.insertWeather(weather)
    }

    suspend fun getWeather(): LiveData<CurrentWeather> {
        return weatherDao.getWeather()
    }

    suspend fun deleteWeather() {
        weatherDao.deleteWeather()
    }



}
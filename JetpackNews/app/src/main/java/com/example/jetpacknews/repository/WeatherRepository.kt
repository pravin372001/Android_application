package com.example.jetpacknews.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.jetpacknews.database.CurrentWeather
import com.example.jetpacknews.database.NewsDatabase
import com.example.jetpacknews.database.WeatherDao

class WeatherRepository(context: Context) {
    private val weatherDao: WeatherDao

    init {
        val database = NewsDatabase.getDatabase(context)
        weatherDao = database.weatherDao()
    }

    suspend fun insert(weather: CurrentWeather) {
        weatherDao.insertWeather(weather)
    }

    fun getWeather(): LiveData<CurrentWeather> {
        return weatherDao.getWeather()
    }

    suspend fun deleteWeather() {
        weatherDao.deleteWeather()
    }

}
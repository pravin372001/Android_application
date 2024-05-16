package com.example.newswithweather.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.newswithweather.database.CurrentWeather
import com.example.newswithweather.model.weather.current.Weather
import com.example.newswithweather.repository.WeatherRepository
import com.example.newswithweather.service.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel(private val repository: WeatherRepository): ViewModel() {

    private val _weatherData = MutableLiveData<Weather>()
    val weatherData: LiveData<Weather> = _weatherData

    fun fetchWeatherData(location: String) {
        viewModelScope.launch {
            try {
                val weatherApiData = withContext(Dispatchers.IO){
                    WeatherApi.retrofitService.getWeather(location, "DnYpJuZR6xWiQUzC0fSInGc1Y7sVcH3E")
                }
                Log.i("WeatherViewModel", "Weather data: $weatherData")
                _weatherData.value = weatherApiData
                formatWeather(weatherApiData)
            } catch (e: Exception){
                Log.e("WeatherViewModel", "Error fetching weather data: ${e.message}")
            }
        }
    }

    private fun formatWeather(weatherApiData: Weather) {
         val currentWeather = CurrentWeather(
            time = weatherApiData.data.time,
            temperature = weatherApiData.data.values.temperature,
            uvIndex = weatherApiData.data.values.uvIndex,
            visibility = weatherApiData.data.values.visibility,
            windDirection = weatherApiData.data.values.windDirection,
            windSpeed = weatherApiData.data.values.windSpeed,
            humidity = weatherApiData.data.values.humidity,
            name = weatherApiData.location.name,
             weatherCode = weatherApiData.data.values.weatherCode
        )
        insertWeather(currentWeather)
    }

    private fun insertWeather(currentWeather: CurrentWeather) {
        viewModelScope.launch {
            repository.insert(currentWeather)
        }
    }

    fun getWeather(): LiveData<CurrentWeather> {
        return repository.getWeather()
    }

    class WeatherViewModelFactory(private val weatherRepository: WeatherRepository): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WeatherViewModel(weatherRepository) as T
        }
    }
}
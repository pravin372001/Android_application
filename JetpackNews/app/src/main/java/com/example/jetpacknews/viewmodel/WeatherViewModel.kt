package com.example.jetpacknews.viewmodel

import android.location.Geocoder
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.jetpacknews.api.WeatherApi
import com.example.jetpacknews.database.CurrentWeather
import com.example.jetpacknews.repository.WeatherRepository
import com.example.jetpacknews.model.current.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel(private val repository: WeatherRepository): ViewModel() {

    private val _weatherData = MutableLiveData<Weather>()
    val weatherData: LiveData<Weather> = _weatherData

    private val _currentWeather = MutableLiveData<CurrentWeather>()
    val currentWeather: LiveData<CurrentWeather> = _currentWeather

    private val locationData = MutableLiveData<String>()
    val location: LiveData<String> = locationData

    private val locationName = MutableStateFlow<String>("")
    val locationNameFlow = locationName.asStateFlow()

    fun fetchWeatherData() {
        viewModelScope.launch {
            try {
                val weatherApiData = withContext(Dispatchers.IO){
                    WeatherApi.retrofitService.getWeather(locationData.value!!, "DnYpJuZR6xWiQUzC0fSInGc1Y7sVcH3E")
                }
                Log.i("WeatherViewModel", "Weather data: ${weatherData.value.toString()}")
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
            weatherCode = weatherApiData.data.values.weatherCode
        )
        insertWeather(currentWeather)
    }

    private fun insertWeather(currentWeather: CurrentWeather) {
        viewModelScope.launch {
            repository.insert(currentWeather)
        }
    }

    fun getWeather(){
        viewModelScope.launch {
            try{
                val currentWeather = withContext(Dispatchers.IO){
                    repository.getWeather()
                }
                currentWeather.observeForever{
                    _currentWeather.value = currentWeather.value
                }
            } catch (e: Exception){
                Log.e("WeatherViewModel", "Error fetching weather data: ${e.message}")
            }
        }
    }

    fun setLocation(location: String) {
        locationData.value = location
    }

    fun setLocationName(cityName: String?) {
        Log.i("WeatherViewModel", "Location name: $cityName")
        locationName.value = cityName ?: ""
    }

    class WeatherViewModelFactory(private val weatherRepository: WeatherRepository): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WeatherViewModel(weatherRepository) as T
        }
    }
}
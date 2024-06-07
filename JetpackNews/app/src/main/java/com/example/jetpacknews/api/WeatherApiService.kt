package com.example.jetpacknews.api

import com.example.jetpacknews.model.current.Weather
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private const val BASE_URL = "https://api.tomorrow.io/v4/"

private const val TIME_BASE_URL = "https://api.tomorrow.io/v4/timelines"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface WeatherApiService {
    @GET("weather/realtime")
    suspend fun getWeather(@Query("location") location: String, @Query("apikey") apiKey: String) : Weather
    @POST(TIME_BASE_URL)
    suspend fun getTimeWeather(@Query("apikey") apiKey: String)
}

object WeatherApi {
    val retrofitService : WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}
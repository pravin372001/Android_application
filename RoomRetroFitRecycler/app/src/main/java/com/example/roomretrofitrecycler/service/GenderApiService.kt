package com.example.roomretrofitrecycler.service

import androidx.lifecycle.LiveData
import com.example.roomretrofitrecycler.model.Gender
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private const val BASE_URL = "https://api.genderize.io/"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface GenderApiService{
    @GET("/")
    suspend fun getData(@Query("name") name: String): Gender
}

object GenderApi{
    val retrofitApiServies :GenderApiService by lazy {
        retrofit.create(GenderApiService::class.java)
    }
}
package com.example.retrofitrecycler

import com.example.retrofitrecycler.model.CountryItem
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET

private const val BASE_URL = "https://restcountries.com/v3.1/"

private val retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

interface CountryApiService {
    @GET("all?fields=name,flags,population")
    suspend fun getCountry(): List<CountryItem>
}

object CountryApi{
    val retrofitApiService:CountryApiService by lazy {
        retrofit.create(CountryApiService::class.java)
    }
}
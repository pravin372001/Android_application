package com.example.znews.remote

import com.example.znews.model.newsone.NewsOne
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://newsdata.io/api/1/"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface NewsOneService {
    @GET("latest")
    suspend fun getNews(
        @Query("apikey") apikey: String,
        @Query("language") language: String = "en",
        @Query("country") country: String = "in"
    ): NewsOne

    @GET("latest")
    suspend fun getNewsByPageNumber(
        @Query("apikey") apikey: String,
        @Query("page") page: Int,
        @Query("language") language: String = "en",
        @Query("country") country: String = "in"
    ): NewsOne
}

object NewsOneApi {
    val retrofitService: NewsOneService by lazy {
        retrofit.create(NewsOneService::class.java)
    }
}

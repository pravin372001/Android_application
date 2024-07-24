package com.example.znews.remote

import com.example.newswithweather.model.news.News
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private const val BASE_URL = "https://inshortsapi.vercel.app/"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface NewsApiService {
    @GET("/news")
    suspend fun getNews(@Query("category") category: String): News

}

object NewsApi {
    val retrofitService: NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java)
    }
}

package com.pravin.mycontact.remote

import android.util.Log
import com.pravin.mycontact.remote.model.Contact
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://randomuser.me/api/"

private val retrofit by lazy {
    try {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    } catch (e: Exception) {
        Log.e("Retrofit", "Error initializing Retrofit", e)
        throw e
    }
}

interface ContactApiService {
    @GET("?results=25&inc=gender,name,picture,phone,cell,id,email")
    suspend fun getContacts(): Contact
}

object ContactApi {
    val retrofitService: ContactApiService by lazy {
        try {
            retrofit.create(ContactApiService::class.java)
        } catch (e: Exception) {
            Log.e("ContactApi", "Error creating Retrofit service", e)
            throw e
        }
    }
}

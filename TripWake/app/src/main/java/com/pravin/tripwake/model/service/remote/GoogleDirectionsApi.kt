package com.pravin.tripwake.model.service.remote

import com.pravin.tripwake.model.map.GooglePlacesInfo
import retrofit2.http.POST
import retrofit2.http.Query

interface GoogleDirectionsApi {
    @POST("/maps/api/directions/json")
    suspend fun getDirection(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") key: String
    ): GooglePlacesInfo

    companion object {
        const val BASE_URL = "https://maps.googleapis.com/"
    }
}
package com.pravin.tripwake.model.service.module

import com.pravin.tripwake.model.service.remote.GoogleDirectionsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapModule {

    @Provides
    @Singleton
    fun provideGoogleDirectionsApi(): GoogleDirectionsApi{
        return Retrofit.Builder()
            .baseUrl(GoogleDirectionsApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleDirectionsApi::class.java)
    }
}
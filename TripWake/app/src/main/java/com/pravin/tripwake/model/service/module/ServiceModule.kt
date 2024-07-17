package com.pravin.tripwake.model.service.module

import com.pravin.tripwake.model.service.AccountService
import com.pravin.tripwake.model.service.MapService
import com.pravin.tripwake.model.service.impl.AccountServiceImpl
import com.pravin.tripwake.model.service.impl.MapServiceImpl
import com.pravin.tripwake.repository.TripRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds abstract fun provideMapService(impl: MapServiceImpl): MapService
}
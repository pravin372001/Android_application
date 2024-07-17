package com.pravin.tripwake.repository

import android.content.Context
import com.pravin.tripwake.database.Trip
import com.pravin.tripwake.database.TripDao
import com.pravin.tripwake.database.TripDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class TripRepository(context: Context) {
    private val tripDao: TripDao = TripDatabase.getDatabase(context).tripDao()

    suspend fun insertTrip(trip: Trip) {
        tripDao.insertTrip(trip)
    }

    suspend fun getAllTrips(): Flow<List<Trip>> {
        return tripDao.getAllTrips()
    }

    suspend fun deleteTrip(tripId: Int) {
        tripDao.deleteTrip(tripId)
    }

    suspend fun getLastTrip(): Trip {
        return tripDao.getLastTrip()
    }



}
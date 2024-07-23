package com.pravin.tripwake.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Insert
    suspend fun insertTrip(trip: Trip)

    @Query("SELECT * FROM trip")
    fun getAllTrips(): Flow<List<Trip>>

    @Query("DELETE FROM trip WHERE id = :tripId")
    suspend fun deleteTrip(tripId: Int)

    @Query("SELECT * FROM trip ORDER BY id DESC LIMIT 1")
    suspend fun getLastTrip(): Trip

    @Query("UPDATE trip SET isTracking = 0 WHERE id = :tripId")
    abstract fun updateTrip(tripId: Int)

}
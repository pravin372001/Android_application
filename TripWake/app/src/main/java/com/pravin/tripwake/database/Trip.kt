package com.pravin.tripwake.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng

@Entity
@TypeConverters(Converters::class)
data class Trip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startLocation: LatLng,
    val endLocation: LatLng,
    val ployLine: List<LatLng>,
    val radius: Float,
    val isTracking: Boolean
)

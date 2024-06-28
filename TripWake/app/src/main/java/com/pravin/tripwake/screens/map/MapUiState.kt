package com.pravin.tripwake.screens.map

import android.os.Parcelable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

data class MapUiState(
    val destinationPoints: LatLng,
    val destination: String,
    val polyline: List<LatLng>,
    val currentLocation: LatLng,
    val radius: Float,
    val isTracking: Boolean
)


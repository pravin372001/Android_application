package com.pravin.tripwake.model.map

data class GeocodedWaypoints(
    val geocoder_status: String,
    val place_id: String,
    val types: List<String>
)

package com.pravin.tripwake.model.map

data class GooglePlacesInfo(
    val geocoded_waypoints: List<GeocodedWaypoints>,
    val routes: List<Routes>,
    val status: String
)

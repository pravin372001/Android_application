package com.pravin.tripwake.model.map

data class Routes(
    val summary: String,
    val overview_polyline: OverviewPolyline,
    val legs: List<Legs>
)

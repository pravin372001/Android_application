package com.pravin.tripwake.model.service

import com.pravin.tripwake.model.map.GooglePlacesInfo

interface MapService {
    suspend fun getDirections(origin: String, destination: String, key: String): GooglePlacesInfo
}
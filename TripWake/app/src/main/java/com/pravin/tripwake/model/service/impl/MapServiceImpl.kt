package com.pravin.tripwake.model.service.impl

import com.pravin.tripwake.model.map.GooglePlacesInfo
import com.pravin.tripwake.model.service.MapService
import com.pravin.tripwake.model.service.remote.GoogleDirectionsApi
import javax.inject.Inject

class MapServiceImpl @Inject constructor(
    private val mapApi: GoogleDirectionsApi
)  : MapService {

    override suspend fun getDirections(origin: String, destination: String, key: String): GooglePlacesInfo {
        return mapApi.getDirection(
            origin = origin,
            destination = destination,
            key = key
        )
    }
}
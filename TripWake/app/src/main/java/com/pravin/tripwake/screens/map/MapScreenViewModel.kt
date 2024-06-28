package com.pravin.tripwake.screens.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.maps.android.compose.CameraPositionState
import com.pravin.tripwake.Screen
import com.pravin.tripwake.model.service.MapService
import com.pravin.tripwake.util.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import com.pravin.tripwake.R.string as AppText

@HiltViewModel
class MapScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mapService: MapService
) :ViewModel() {

    private val placesClient = Places.createClient(context)
    private val token = AutocompleteSessionToken.newInstance()

    private val _searchUiState = MutableStateFlow(SearchUiState())
    val searchUiState: StateFlow<SearchUiState> = _searchUiState

    var uiState = mutableStateOf(MapUiState(
        destinationPoints = LatLng(0.0, 0.0),
        destination = "Destination",
        polyline = listOf(),
        currentLocation = LatLng(0.0, 0.0),
        radius = 1000.0f,
        isTracking = false,
    ))
        private set


    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private fun onPolylineChange(newValue: List<LatLng>) {
        uiState.value = uiState.value.copy(polyline = newValue)
    }

    private fun onDestinationPointsChange(newValue: LatLng) {
        Log.d("MapScreenViewModel", "onDestinationPointsChange: $newValue")
        uiState.value = uiState.value.copy(destinationPoints = newValue)
    }

    fun onDestinationChange(newValue: String) {
        uiState.value = uiState.value.copy(destination = newValue)
    }

    fun onRadiusChange(newValue: Float) {
        uiState.value = uiState.value.copy(radius = newValue)
    }

    fun onIsTrackingChange(newValue: Boolean) {
        uiState.value = uiState.value.copy(isTracking = newValue)
    }

    private fun onMyLocationClick(newValue: LatLng) {
        uiState.value = uiState.value.copy(currentLocation = newValue)
        Log.d("MapScreenViewModel", "onMyLocationClick: ${uiState.value.toString()}")
    }

    fun onCreateTripClicked(openAndPopUp: (String, String) -> Unit) {
        if (uiState.value.destination.isEmpty()) {
            SnackbarManager.showMessage(AppText.empty_destination_error)
            return
        }
    }

    fun getDirections(origin: String, destination: String, key: String) {
        viewModelScope.launch {
            try {
                val directions = mapService.getDirections(origin, destination, key)
                Log.d("MapScreenViewModel", "getDirections: $directions")

                val routes = directions.routes
                if (routes.isNotEmpty()) {
                    val overviewPolyline = routes[0].overview_polyline
                    val points = overviewPolyline?.points
                    if (points != null) {
                        Log.d("MapScreenViewModel", "getDirections: $points")
                        decoPoints(points)
                        animateToCurrentLocation(currentLocation = uiState.value.destinationPoints)
                    } else {
                        Log.e("MapScreenViewModel", "OverviewPolyline is null or does not contain points")
                    }
                } else {
                    Log.e("MapScreenViewModel", "Routes list is empty")
                }
            } catch (e: Exception) {
                Log.e("MapScreenViewModel", "Error getting directions: ${e.message}")
            }
        }
    }

    private fun decoPoints(points: String){
        val poly = decodePoly(points)
        onPolylineChange(poly)
    }

    fun onQueryChanged(query: String) {
        _searchUiState.value = _searchUiState.value.copy(query = query)
        if (query.isNotEmpty()) {
            searchPlaces(query)
        } else {
            _searchUiState.value = _searchUiState.value.copy(predictions = listOf())
        }
    }

    private fun searchPlaces(query: String) {
        viewModelScope.launch {
            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(query)
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    _searchUiState.value = _searchUiState.value.copy(predictions = response.autocompletePredictions)
                }
                .addOnFailureListener { exception ->
                    Log.e("MapScreenViewModel", "Error searching places", exception)
                }
        }
    }

    suspend fun animateToCurrentLocation(
        cameraPositionState: CameraPositionState = CameraPositionState(),
        currentLocation: LatLng = uiState.value.currentLocation
    ) {
        val zoom = if(currentLocation == uiState.value.currentLocation) 15f else 10f
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(currentLocation, zoom),
        )
    }

    private suspend fun getLatLngFromCityName(context: Context, cityName: String) {
        withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(cityName, 1)
                Log.d("MapScreenViewModel", "getLatLngFromCityName: $addresses")
                if (addresses?.isNotEmpty() == true) {
                    val location = addresses[0]
                    onDestinationPointsChange(LatLng(location.latitude, location.longitude))
                    Log.d("MapScreenViewModel", "getLatLngFromCityName Destination: ${uiState.value.destinationPoints}")
                    Log.d("MapScreenViewModel", "getLatLngFromCityName Current: ${uiState.value.currentLocation}")
                    getDirections("${uiState.value.currentLocation.latitude}, ${uiState.value.currentLocation.longitude}", "${uiState.value.destinationPoints.latitude}, ${uiState.value.destinationPoints.longitude}", API.MapAPIKey)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                lng.toDouble() / 1E5)
            poly.add(p)
        }
        return poly
    }

    @SuppressLint("MissingPermission")
    fun fetchCurrentLocation() {
        viewModelScope.launch {
            try {
                val location = fusedLocationClient.lastLocation.await()
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    onMyLocationClick(currentLatLng)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onPlaceSelected(placeName: String, navigateToMap: (String) -> Unit) {
        uiState.value = uiState.value.copy(destination = placeName)
        Log.d("MapScreenViewModel", "onPlaceSelected: ${uiState.value.toString()}")
        viewModelScope.launch {
            getLatLngFromCityName(context, placeName)
        }
        navigateToMap(Screen.Map.route)
    }
}
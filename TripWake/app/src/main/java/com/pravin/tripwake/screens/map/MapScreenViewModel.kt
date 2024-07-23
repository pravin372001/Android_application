package com.pravin.tripwake.screens.map

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.maps.android.compose.CameraPositionState
import com.pravin.tripwake.Screen
import com.pravin.tripwake.database.Trip
import com.pravin.tripwake.listener.ActivityProvider
import com.pravin.tripwake.listener.TrackingListener
import com.pravin.tripwake.model.service.MapService
import com.pravin.tripwake.repository.TripRepository
import com.pravin.tripwake.util.snackbar.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
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
    private val mapService: MapService,
    private val tripRepository: TripRepository
) :ViewModel(), TrackingListener {

    private val placesClient = Places.createClient(context)
    private val token = AutocompleteSessionToken.newInstance()
    private val settingsClient: SettingsClient = LocationServices.getSettingsClient(context)
    private var _tripList = MutableStateFlow<List<Trip>>(mutableListOf())
            val tripList: StateFlow<List<Trip>> = _tripList

    private val _searchUiState = MutableStateFlow(SearchUiState())
    val searchUiState: StateFlow<SearchUiState> = _searchUiState

    private var cameraPositionState: CameraPositionState? = null

    var uiState = mutableStateOf(MapUiState(
        tripId = 0,
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

    private fun onIsTrackingChange(newValue: Boolean) {
        Log.d("MapScreenViewModel", "onIsTrackingChange: $newValue")
        uiState.value = uiState.value.copy(isTracking = newValue)
        updateTrip(uiState.value.tripId)
    }

    override fun onTrackingChanged(isTracking: Boolean) {
        Log.d("MapScreenViewModel", "onTrackingChanged: $isTracking")
        onIsTrackingChange(isTracking)
    }

    private fun onMyLocationClick(newValue: LatLng) {
        uiState.value = uiState.value.copy(currentLocation = newValue)
        Log.d("MapScreenViewModel", "onMyLocationClick: ${uiState.value.toString()}")
    }

    fun onCreateTripClicked(openAndPopUp: (String, String) -> Unit) {
        if (uiState.value.destination.isEmpty() || uiState.value.destination == "Destination") {
            SnackbarManager.showMessage(AppText.empty_destination_error)
            return
        } else {
            onCreateTrip(openAndPopUp)
        }
    }

    private fun onCreateTrip(openAndPopUp: (String, String) -> Unit) {
        uiState.value = uiState.value.copy(isTracking = true)
        val trip = Trip(
            startLocation = uiState.value.currentLocation,
            endLocation = uiState.value.destinationPoints,
            ployLine = uiState.value.polyline,
            radius = uiState.value.radius.toFloat(),
            isTracking = uiState.value.isTracking
        )
        viewModelScope.launch {
            tripRepository.insertTrip(trip)
            openAndPopUp(Screen.Main.route, Screen.Map.route)
        }
    }

    fun getAllTrips() {
        viewModelScope.launch {
            tripRepository.getAllTrips().collect {
                _tripList.value = it
            }
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
                    val points = overviewPolyline.points
                    if (points != null) {
                        Log.d("MapScreenViewModel", "getDirections: $points")
                        decoPoints(points)
                        Log.d("MapScreenViewModel", "before animation")
                        animateToCurrentLocation(cameraPositionState!!, currentLocation = uiState.value.destinationPoints)
                        Log.d("MapScreenViewModel", "after animation")
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

    fun onselectTrip(trip: Trip){
        val destination = getPlaceNameByLatLan(trip.endLocation.latitude, trip.endLocation.longitude)
        uiState.value = uiState.value.copy(tripId = trip.id,destinationPoints = trip.endLocation, currentLocation = trip.startLocation, polyline = trip.ployLine, radius = trip.radius, isTracking = trip.isTracking, destination = destination)
    }

    suspend fun animateToCurrentLocation(
        cameraPositionState: CameraPositionState,
        currentLocation: LatLng = uiState.value.currentLocation
    ) {
        this.cameraPositionState = cameraPositionState
        Log.d("MapScreenViewModel", "animateToCurrentLocation: $currentLocation")
        val zoom = if(currentLocation == uiState.value.currentLocation) 15f else 10f
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(currentLocation, zoom),
        )
        Log.d("MapScreenViewModel", "animateToCurrentLocation: $currentLocation")
        Log.d("MapScreenViewModel", "animateToCurrentLocation: ${uiState.value.currentLocation}")
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
                } ?: run {
                    // If location is null, check if GPS is enabled
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val REQUEST_CHECK_SETTINGS = 100
    }


    fun onPlaceSelected(placeName: String, navigateToMap: (String) -> Unit) {
        uiState.value = uiState.value.copy(destination = placeName)
        Log.d("MapScreenViewModel", "onPlaceSelected: ${uiState.value.toString()}")
        viewModelScope.launch {
            getLatLngFromCityName(context, placeName)
        }
        navigateToMap(Screen.Map.route)
    }

    fun getPlaceNameByLatLan(lat: Double, lan: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lan, 1)
        return addresses?.get(0)?.getAddressLine(0) ?: ""
    }

    fun updateTrip(tripId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            tripRepository.updateTrip(tripId)
        }
    }

    fun resetUiState() {
        uiState.value = uiState.value.copy(
            destinationPoints = LatLng(0.0, 0.0),
            destination = "Destination",
            polyline = listOf(),
            radius = 1000.0f,
            isTracking = false,
        )
    }
}
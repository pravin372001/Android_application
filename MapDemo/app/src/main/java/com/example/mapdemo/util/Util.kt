package com.example.mapdemo.util

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng


fun getLastKnownLocation(
    context: Context,
    onLocationReceived: (LatLng) -> Unit
    ){

    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    loc ->
                    onLocationReceived(LatLng(loc.latitude, loc.longitude))
                    Log.d("location -> Util", loc.toString())
                } ?: kotlin.run {
                    Log.d("location -> Util", "Location is null")
                }
            }
            .addOnFailureListener {
                Log.d("location -> Util", "Failed to get location")
            }
    } else{
        Log.d("location -> Util", "Permission not granted")
    }
}
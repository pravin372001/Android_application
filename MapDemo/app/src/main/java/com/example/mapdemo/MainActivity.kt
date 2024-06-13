package com.example.mapdemo

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mapdemo.ui.theme.MapDemoTheme
import com.example.mapdemo.util.getLastKnownLocation
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


private val LOCATION_PERMISSION_REQUEST_CODE = 123
private var getsLocation : Boolean = false
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLocationPermission()
        setContent {
            MapDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { it->
                    MapScreen(modifier = Modifier.padding(it))
                }
            }
        }
    }
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
        getsLocation = true

    }
}

@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    var showMap by remember { mutableStateOf(false) }
    var location by remember { mutableStateOf(LatLng(0.0, 0.0)) }

    getLastKnownLocation(LocalContext.current) {
        location = it
        showMap = true
    }

    if (showMap){
        MyMap(
            modifier = modifier,
            latLng = location
        )
    } else {
        Text(
            modifier = Modifier.fillMaxSize() ,
            text = "Loading...",
            color = androidx.compose.ui.graphics.Color.White
            )

    }
}

@Composable
fun MyMap(
    modifier: Modifier = Modifier,
    latLng: LatLng,
) {

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 15f)
    }
    Surface(modifier = modifier) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(),
        ){
            Marker(
                state = MarkerState(position = latLng),
            )
        }
    }
}
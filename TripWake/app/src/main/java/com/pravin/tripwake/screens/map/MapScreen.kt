package com.pravin.tripwake.screens.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneOutline
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.pravin.tripwake.MainActivity
import com.pravin.tripwake.R
import com.pravin.tripwake.Screen
import com.pravin.tripwake.model.service.workmanager.TripWorkManager
import com.pravin.tripwake.ui.theme.TripWakeTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    openAndPopUp: (String, String) -> Unit,
    navigateToSearch: (String) -> Unit,
    viewModel: MapScreenViewModel
    ) {

    val uiState = viewModel.uiState

    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(uiState.value.currentLocation, 15f)
    }
    val context = LocalContext.current
    val activity = context as MainActivity
    val coroutineScope = rememberCoroutineScope()
    Log.d("uiState", "MapScreen: ${uiState.value.destination}")
    if(uiState.value.isTracking){
        LaunchedEffect(Unit) {
            activity.requestNotificationPermission {
                Log.d("onCreateTrip", "MapScreen: before schedule")
                scheduleLocationCheck(
                    context,
                    destinationLat = uiState.value.destinationPoints.latitude,
                    destinationLon = uiState.value.destinationPoints.longitude,
                    radius = uiState.value.radius)
                Log.d("onCreateTrip", "MapScreen: after schedule")
            }
        }
    }
    MapScreenContent(
        modifier = modifier,
        openAndPopUp = openAndPopUp,
        cameraPositionState = cameraPositionState,
        navigateToSearch = navigateToSearch,
        uiState = uiState.value,
        onRadiusChange = { viewModel.onRadiusChange(it) },
        onLocationClicked = { viewModel.fetchCurrentLocation() },
        onCreateTrip = {
            viewModel.onCreateTripClicked(openAndPopUp = openAndPopUp)
                       },
        animateCurrentLocation = {
            coroutineScope.launch {
                viewModel.animateToCurrentLocation(it)
            }
        }
        )
}

object API{
    const val MapAPIKey = "AIzaSyBpDkR1Nxe2NV6OeI9FKQYZfWcOZNAgN5c"
}

@Composable
fun MapScreenContent(
    modifier: Modifier = Modifier,
    openAndPopUp: (String, String) -> Unit,
    cameraPositionState: CameraPositionState,
    navigateToSearch: (String) -> Unit,
    uiState: MapUiState,
    onRadiusChange: (Float) -> Unit,
    onLocationClicked: () -> Unit,
    onCreateTrip:() -> Unit,
    animateCurrentLocation: (CameraPositionState) -> Unit,
    ) {
    Log.d("uiState", "MapScreenContent: ${uiState.destination}")
    val currentLocation = uiState.currentLocation
    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                compassEnabled = true,
                zoomControlsEnabled = false
            )
        )
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                onBackClick = {
                    openAndPopUp(Screen.Main.route, Screen.Map.route)
                },
                uiState = uiState,
                navigateToSearch = navigateToSearch,
                onRadiusChange = onRadiusChange
            )
        }
    ) { it ->
        Surface(
            modifier = modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                GoogleMap(
                    modifier = modifier,
                    cameraPositionState = cameraPositionState,
                    uiSettings = uiSettings,
                ) {
                    Marker(
                        position = currentLocation,
                        title = "My Location",
                        snippet = "Current Location",
                        icon = bitmapFromVector(LocalContext.current, R.drawable.placeholder),
                    )
                    if(uiState.destinationPoints != LatLng(0.0, 0.0)) {
                        Marker(
                            position = uiState.destinationPoints,
                            title = "Destination",
                            snippet = "Destination",
                            icon = bitmapFromVector(LocalContext.current, R.drawable.destination)
                        )
                        Circle(
                            center = uiState.destinationPoints,
                            fillColor = Color(0x220000FF),
                            radius = uiState.radius.toDouble(),
                            strokeColor = Color.Red,
                            strokeWidth = 2f
                            )
                    }
                    Polyline(points = uiState.polyline, color = Color.Blue)

                }
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomEnd,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        FloatingActionButton(onClick = {
                            onLocationClicked()
                            animateCurrentLocation(cameraPositionState)
                        }
                        ) {
                            Icon(imageVector = Icons.Filled.MyLocation, contentDescription = null)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if(!uiState.isTracking){
                            FloatingActionButton(onClick = {
                                onCreateTrip()
                            }) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "Create Trip")
                                    Icon(
                                        imageVector = Icons.Filled.DoneOutline,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun scheduleLocationCheck(context: Context, destinationLat: Double, destinationLon: Double, radius: Float) {
    Log.d("scheduleLocationCheck", "scheduleLocationCheck: $destinationLat")
    Log.d("scheduleLocationCheck", "scheduleLocationCheck: $destinationLon")
    Log.d("scheduleLocationCheck", "scheduleLocationCheck: $radius")
    val inputData = workDataOf(
        "destination_lat" to destinationLat,
        "destination_lon" to destinationLon,
        "radius" to radius
    )

    val locationWorkRequest = OneTimeWorkRequestBuilder<TripWorkManager>()
        .setInputData(inputData)
        .build()

    WorkManager.getInstance(context).enqueue(locationWorkRequest)
}

private fun bitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
    // below line is use to generate a drawable.
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
    // below line is use to set bounds to our vector drawable.
    vectorDrawable!!.setBounds(0, 0, 96, 96)

    // below line is use to create a bitmap for our drawable which we have added.
    val bitmap = Bitmap.createBitmap(
        96,
        96,
        Bitmap.Config.ARGB_8888
    )
    Log.d("bitmapFromVector", "bitmapFromVector: ${vectorDrawable.intrinsicWidth.toString()}")
    Log.d("bitmapFromVector", "bitmapFromVector: ${vectorDrawable.intrinsicHeight.toString()}")

    // below line is use to add bitmap in our canvas.
    val canvas = Canvas(bitmap)

    // below line is use to draw our vector drawable in canvas.
    vectorDrawable.draw(canvas)

    // after generating our bitmap we are returning our bitmap.
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@Composable
fun CustomTopBar(
    onBackClick: () -> Unit,
    uiState: MapUiState,
    navigateToSearch: (String) -> Unit,
    onRadiusChange: (Float) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
        ,
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Image(
                        imageVector = Icons.Filled.MyLocation,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                    ElevatedSuggestionChip(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .size(width = 200.dp, height = 35.dp),
                        label = {
                            Text(
                                text = "Your Location",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp
                            )
                        },
                        onClick = {

                        },
                        enabled = false,
                        )
                }

                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(modifier = Modifier
                    .height(1.dp)
                    .width(300.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(48.dp))
                    Icon(
                        imageVector = Icons.Filled.PinDrop,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    ElevatedSuggestionChip(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .size(width = 200.dp, height = 35.dp),
                        onClick = {
                            navigateToSearch(Screen.Search.route)
                        },
                        label = {
                            Log.d("uiState", "CustomTopBar: ${uiState.destination}")
                            Text(
                                text = uiState.destination,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Start,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        enabled = !uiState.isTracking,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(modifier = Modifier.width(300.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Radius",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        modifier = Modifier.width(200.dp),
                        value = uiState.radius,
                        onValueChange = {
                            onRadiusChange(it)
                        },
                        enabled = !uiState.isTracking,
                        valueRange = 1000f..10000f
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${uiState.radius.toInt()/1000} Km",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun MapScreenPreview() {
    TripWakeTheme {
        MapScreenContent(
            openAndPopUp = { _, _ -> },
            navigateToSearch = {},
            uiState = MapUiState(
                tripId = 0,
                destinationPoints = LatLng(0.0, 0.0),
                destination = "Destination",
                polyline = listOf(),
                currentLocation = LatLng(0.0, 0.0),
                radius = 0.0f,
                isTracking = false,
            ),
            cameraPositionState = CameraPositionState(),
            onRadiusChange = {},
            onLocationClicked = {},
            onCreateTrip = {},
            animateCurrentLocation = {},
        )
    }
}
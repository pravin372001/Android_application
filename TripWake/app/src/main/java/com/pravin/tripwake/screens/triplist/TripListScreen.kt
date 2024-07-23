package com.pravin.tripwake.screens.triplist

import android.Manifest
import android.util.Log
import android.view.Display.Mode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.pravin.tripwake.Screen
import com.pravin.tripwake.database.Trip
import com.pravin.tripwake.screens.map.MapScreenViewModel
import com.pravin.tripwake.ui.theme.TripWakeTheme
import com.pravin.tripwake.util.snackbar.SnackbarManager
import com.pravin.tripwake.R.string as AppText

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TripListScreen(
    modifier: Modifier = Modifier,
    openAndPopUp: (String) -> Unit,
    viewModel: MapScreenViewModel
) {
    Log.d("MapScreen", "uiState: $viewModel")
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    if (locationPermissionState.status.isGranted) {
        LaunchedEffect(Unit) {
            Log.d("MapScreen", "LaunchedEffect: ")
            viewModel.fetchCurrentLocation()
        }
    }

    Log.d("MapScreen", "uiState value: ${viewModel.uiState.value.toString()}")
    viewModel.getAllTrips()
    val trips = viewModel.tripList.collectAsState(initial = emptyList())
    TripListScreenContent(
        modifier = modifier,
        openAndPopUp = openAndPopUp,
        tripList = trips.value,
        getPlaceName = viewModel::getPlaceNameByLatLan,
        onTripItemClick = viewModel::onselectTrip,
        updateTrip = viewModel::updateTrip,
        resetUiState = viewModel::resetUiState
        )
}

@Composable
fun TripListScreenContent(
    modifier: Modifier = Modifier,
    openAndPopUp: (String) -> Unit,
    tripList: List<Trip>,
    getPlaceName: (Double, Double) -> String,
    onTripItemClick: (Trip) -> Unit,
    updateTrip: (Int) -> Unit,
    resetUiState: () -> Unit = {}
) {
    LazyColumn {
        items(tripList.reversed()) {
            TripItem(
                trip = it,
                getPlaceName = getPlaceName,
                openAndPopUp = openAndPopUp,
                onTripItemClick = onTripItemClick,
                updateTrip = updateTrip
            )
        }
    }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = {
                if(tripList.isEmpty() || !tripList.last().isTracking){
                    openAndPopUp(Screen.Map.route)
                    resetUiState()
                } else {
                    SnackbarManager.showMessage(AppText.trip_is_tracking)
                }
            },
            modifier = modifier.padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TripListScreenPreview() {
    TripWakeTheme {
        TripListScreenContent(
            openAndPopUp = { _ -> },
            tripList = listOf(),
            getPlaceName = { _, _ -> "" },
            onTripItemClick = {},
            updateTrip = {}
        )
    }
}
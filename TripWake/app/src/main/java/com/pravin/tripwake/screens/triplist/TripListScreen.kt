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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TripListScreen(
    modifier: Modifier = Modifier,
    openAndPopUp: (String) -> Unit,
    viewModel: MapScreenViewModel
) {
    Log.d("MapScreen", "uiState: $viewModel")
    Log.d("MapScreen", "uiState value: ${viewModel.uiState.value.toString()}")
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    if (locationPermissionState.status.isGranted) {
        LaunchedEffect(Unit) {
            viewModel.fetchCurrentLocation()
        }
    }

    viewModel.getAllTrips()
    TripListScreenContent(
        modifier = modifier,
        openAndPopUp = openAndPopUp,
        tripList = viewModel.tripList,
        getPlaceName = viewModel::getPlaceNameByLatLan,
        onTripItemClick = viewModel::onselectTrip
        )
}

@Composable
fun TripListScreenContent(
    modifier: Modifier = Modifier,
    openAndPopUp: (String) -> Unit,
    tripList: Flow<List<Trip>>,
    getPlaceName: (Double, Double) -> String,
    onTripItemClick: (Trip) -> Unit,
) {
    val list = tripList.collectAsState(initial = emptyList())
    LazyColumn {
        items(list.value) {
            TripItem(
                trip = it,
                getPlaceName = getPlaceName,
                openAndPopUp = openAndPopUp,
                onTripItemClick = onTripItemClick
            )
        }
    }
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = {
                openAndPopUp(Screen.Map.route)
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
            tripList = flowOf(listOf()),
            getPlaceName = { _, _ -> "" },
            onTripItemClick = {}
        )
    }
}
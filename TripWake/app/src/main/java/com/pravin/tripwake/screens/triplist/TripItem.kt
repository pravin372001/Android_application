package com.pravin.tripwake.screens.triplist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pravin.tripwake.Screen
import com.pravin.tripwake.database.Trip
import com.pravin.tripwake.ui.theme.TripWakeTheme
import com.pravin.tripwake.util.snackbar.SnackbarManager
import com.pravin.tripwake.R.string as AppText

@Composable
fun TripItem(
    openAndPopUp: (String) -> Unit,
    modifier: Modifier = Modifier,
    trip: Trip,
    getPlaceName: (Double, Double) -> String,
    onTripItemClick: (Trip) -> Unit,
    updateTrip: (Int) -> Unit
) {
    val startLocation = getPlaceName(trip.startLocation.latitude, trip.startLocation.longitude)
    val endLocation = getPlaceName(trip.endLocation.latitude, trip.endLocation.longitude)
    TripItemContent(
        openAndPopUp = openAndPopUp,
        modifier = modifier,
        trip = trip,
        startLocation = startLocation,
        endLocation = endLocation,
        onTripItemClick = onTripItemClick,
        updateTrip = updateTrip
    )
}

@Composable
fun TripItemContent(
    openAndPopUp: (String) -> Unit,
    modifier: Modifier,
    trip: Trip,
    startLocation: String,
    endLocation: String,
    onTripItemClick: (Trip) -> Unit,
    updateTrip: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                if (trip.isTracking){
                    openAndPopUp(Screen.Map.route)
                    onTripItemClick(trip)
                } else {
                    SnackbarManager.showMessage(AppText.trip_is_finished)
                }
            }
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
        ) {
            var expanded by remember {  mutableStateOf(false) }
            if (trip.isTracking){
                IconButton(
                    onClick = {
                        expanded = !expanded
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                            text = { Text("Finish Trip") },
                            onClick = {
                                expanded = false
                                updateTrip(trip.id)
                            }
                        )
                    }
                }
            }
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Start Destination",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = startLocation,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "End Destination",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = endLocation,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if(trip.isTracking){
                TrackingIndicator(
                    Color.Green,
                    "Tracking"
                )
            } else {
                TrackingIndicator(
                    Color.Blue,
                    "Trip Finished"
                )
            }

        }
    }
}

@Composable
fun TrackingIndicator(
    color: Color,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            color = color,
            shape = CircleShape,
            modifier = Modifier
                .size(16.dp)
                .padding(end = 8.dp)
        ) {}
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Preview
@Composable
private fun TripItemPreview() {
    TripWakeTheme {
        TripItemContent(
            modifier = Modifier,
            startLocation = "Start Location",
            endLocation = "End Location",
            openAndPopUp = { _ -> },
            trip = Trip(
                startLocation = com.google.android.gms.maps.model.LatLng(0.0, 0.0),
                endLocation = com.google.android.gms.maps.model.LatLng(0.0, 0.0),
                ployLine = listOf(),
                radius = 0.0f,
                isTracking = false
            ),
            onTripItemClick = {},
            updateTrip = {}
        )
    }
}
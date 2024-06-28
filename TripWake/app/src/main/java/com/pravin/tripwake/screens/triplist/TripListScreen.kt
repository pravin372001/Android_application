package com.pravin.tripwake.screens.triplist

import android.view.Display.Mode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pravin.tripwake.Screen
import com.pravin.tripwake.ui.theme.TripWakeTheme

@Composable
fun TripListScreen(
    modifier: Modifier = Modifier,
    openAndPopUp: (String) -> Unit,
) {

    TripListScreenContent(
        modifier = modifier,
        openAndPopUp = openAndPopUp
        )
}

@Composable
fun TripListScreenContent(
    modifier: Modifier = Modifier,
    openAndPopUp: (String) -> Unit,
) {
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
        TripListScreen(
            openAndPopUp = { _ -> }
        )
    }
}
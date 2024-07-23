package com.pravin.tripwake.screens.map

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import com.pravin.tripwake.Screen
import com.pravin.tripwake.ui.theme.TripWakeTheme

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    openAndPopUp: (String, String) -> Unit,
    navigateToMap: (String) -> Unit,
    viewModel: MapScreenViewModel
) {
    val uiState by viewModel.searchUiState.collectAsState()

    Log.d("SearchScreen", "uiState: $viewModel")
    SearchScreenContent(
        uiState = uiState,
        onBackClick = openAndPopUp,
        onPlaceSelected = { viewModel.onPlaceSelected(it, navigateToMap) },
        onQueryChanged = { viewModel.onQueryChanged(it) },
        )
}

@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun SearchScreenContent(
    uiState: SearchUiState,
    onPlaceSelected: (String) -> Unit,
    onBackClick: (String, String) -> Unit,
    onQueryChanged: (String) -> Unit,
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = { onQueryChanged(it) },
            placeholder = { Text(text = "Search") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                IconButton(onClick = { onBackClick(Screen.Map.route, Screen.Search.route) }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = null )
                }
            },
            trailingIcon = {
                IconButton(onClick = { onQueryChanged("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null
                    )
                }
            },
            singleLine = true,
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(uiState.predictions.size) { index ->
                val place = uiState.predictions[index]
                val placeName = place.getFullText(null).toString()
                SearchItem(
                    placeName = placeName,
                    onPlaceSelected = onPlaceSelected,
                )
                HorizontalDivider(modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun SearchItem(
    modifier: Modifier = Modifier,
    placeName: String,
    onPlaceSelected: (String) -> Unit,
    ) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onPlaceSelected(placeName)
            }
    ) {
        Text(text = placeName)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { onPlaceSelected(placeName) }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenContentPreview() {
    TripWakeTheme {
        SearchScreenContent(
            uiState = SearchUiState(),
            onQueryChanged = {},
            onPlaceSelected = {},
            onBackClick = { _, _ -> {} },
        )
    }
}


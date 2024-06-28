package com.pravin.tripwake.screens.map

import com.google.android.libraries.places.api.model.AutocompletePrediction

data class SearchUiState(
    val query: String = "",
    val predictions: List<AutocompletePrediction> = emptyList()
)

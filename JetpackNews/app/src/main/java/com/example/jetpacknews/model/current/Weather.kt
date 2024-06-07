package com.example.jetpacknews.model.current

import com.example.jetpacknews.model.current.Data
import com.example.jetpacknews.model.current.Location

data class Weather(
    val `data`: Data,
    val location: Location
)
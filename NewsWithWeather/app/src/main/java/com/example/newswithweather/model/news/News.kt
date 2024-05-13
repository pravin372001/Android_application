package com.example.newswithweather.model.news

data class News(
    val category: String,
    val `data`: List<Data>,
    val success: Boolean
)
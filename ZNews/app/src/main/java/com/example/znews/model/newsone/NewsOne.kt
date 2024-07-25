package com.example.znews.model.newsone

data class NewsOne(
    val nextPage: String,
    val results: List<Result>,
    val status: String,
    val totalResults: Int
)
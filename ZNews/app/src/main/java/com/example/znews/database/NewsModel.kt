package com.example.znews.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_table")
data class NewsModel(
    @PrimaryKey(autoGenerate = true)
    var newsId: Int = 0,
    val category: String,
    val author: String,
    val content: String,
    val date: String,
    val id: String,
    val imageUrl: String,
    val readMoreUrl: String,
    val time: String,
    val title: String,
    val url: String,
    val success: Boolean
)
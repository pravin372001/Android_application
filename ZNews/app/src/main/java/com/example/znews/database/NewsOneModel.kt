package com.example.znews.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "newsone_table")
data class NewsOneModel (
    @PrimaryKey(autoGenerate = true)
    var newsId: Int = 0,
    val title: String,
    val category: String,
    val content: String,
    val country: String,
    val creator: String,
    val description: String,
    val image_url: String,
    val pubDate: String,
    val link: String,
    )
package com.example.znews.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NewsDao {

    @Insert
    suspend fun insertNews(news: NewsModel)

    @Query("SELECT * FROM news_table")
    fun getAllNews(): LiveData<List<NewsModel>>

    @Query("SELECT * FROM news_table WHERE category = :category")
    fun getNewsByCategory(category: String): LiveData<List<NewsModel>>

    @Query("DELETE FROM news_table")
    suspend fun deleteAllNews()

    @Query("UPDATE sqlite_sequence SET SEQ=0 WHERE NAME='news_table'")
    suspend fun resetTable()
}
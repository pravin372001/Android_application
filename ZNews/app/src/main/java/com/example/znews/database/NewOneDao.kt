package com.example.znews.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NewOneDao {

    @Insert
    suspend fun insertNews(news: NewsOneModel)

    @Query("SELECT * FROM newsone_table")
    fun getAllNews(): LiveData<List<NewsOneModel>>

    @Query("DELETE FROM newsone_table")
    suspend fun deleteAllNews()

    @Query("UPDATE sqlite_sequence SET SEQ=0 WHERE NAME='newsone_table'")
    suspend fun resetTable()

    @Query("SELECT * FROM newsone_table WHERE category = :category")
    fun getNewsByCategory(category: String): LiveData<List<NewsOneModel>>


}
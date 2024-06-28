package com.example.jetpacknews.database

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NewsDao {
    @Insert
    suspend fun insertNews(news : NewsModel)

    @Query("SELECT * FROM news_table")
    fun getAllNews() : LiveData<List<NewsModel>>

    @Query("SELECT * FROM news_table WHERE category = :category LIMIT :pageSize OFFSET :offset")
    fun getPaginatedNews(offset: Int, pageSize: Int, category: String): LiveData<List<NewsModel>>

    @Query("SELECT * FROM news_table WHERE category = :category")
    fun getNewsByCategory(category : String) : LiveData<List<NewsModel>>

    @Query("DELETE FROM news_table")
    suspend fun deleteAllNews()

    @Query("UPDATE sqlite_sequence SET SEQ=0 WHERE NAME='news_table'")
    suspend fun resetTable()

    @Query("SELECT * FROM news_table WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun filterNews(query: String?) : LiveData<List<NewsModel>>

    @Query("""
            SELECT * FROM news_table
            ORDER BY
            date DESC,
        time DESC
                LIMIT :pageSize OFFSET :page
        """)
    fun getPaginatedAll(page: Int, pageSize: Int): LiveData<List<NewsModel>>


    @Query("SELECT * FROM news_table WHERE date = :date AND time = :time LIMIT 1")
    suspend fun getNewsByDateTime(date: String, time: String): NewsModel?

    @Query("SELECT * FROM news_table ORDER BY date DESC, time DESC")
    fun getPagingAllNews(): PagingSource<Int, NewsModel>

    @Query("SELECT * FROM news_table WHERE category = :category ORDER BY date DESC, time DESC")
    fun getPagingNewsByCategory(category: String): PagingSource<Int, NewsModel>

    @Query("SELECT * FROM news_table WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY date DESC, time DESC")
    fun getPagingFilteredNews(query: String): PagingSource<Int, NewsModel>
}
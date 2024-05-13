package com.example.newswithweather.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.newswithweather.database.NewsDao
import com.example.newswithweather.database.NewsDatabase
import com.example.newswithweather.database.NewsModel

class NewsRepository(context: Context) {
    private val newsDao :NewsDao

    init {
        val database =NewsDatabase.getDatabase(context)
        newsDao = database.newsDao()
    }

    suspend fun insertNews(news: NewsModel){
        newsDao.insertNews(news)
    }

    fun getAllNews(): LiveData<List<NewsModel>>{
        return newsDao.getAllNews()
    }

    suspend fun deleteAllNews(){
        newsDao.deleteAllNews()
    }

    fun getNewsByCategory(category: String): LiveData<List<NewsModel>> {
        return newsDao.getNewsByCategory(category)
    }

    fun getPaginatedNews(offset: Int, pageSize: Int, category: String): LiveData<List<NewsModel>> {
        return newsDao.getPaginatedNews(offset, pageSize, category)
    }

    suspend fun reset() {
        newsDao.resetTable()
    }
}
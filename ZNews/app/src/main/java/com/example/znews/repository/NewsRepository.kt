package com.example.znews.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.znews.database.NewsDao
import com.example.znews.database.NewsDatabase
import com.example.znews.database.NewsModel

class NewsRepository(context: Context) {
    private val newsDao: NewsDao

    init {
        val database = NewsDatabase.getDatabase(context)
        newsDao = database.newsDao()
    }

    fun getAllNews(): LiveData<List<NewsModel>> {
        return newsDao.getAllNews()
    }

    fun getNewsByCategory(category: String): LiveData<List<NewsModel>> {
        if(category == "all") {
            return newsDao.getAllNews()
        }
        return newsDao.getNewsByCategory(category)
    }

    suspend fun insertNews(newsList: List<NewsModel>) {
        for (news in newsList) {
            newsDao.insertNews(news)
        }
    }

    suspend fun deleteAllNews() {
        newsDao.resetTable()
        newsDao.deleteAllNews()
    }
}
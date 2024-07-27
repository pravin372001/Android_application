package com.example.znews.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.znews.database.NewOneDao
import com.example.znews.database.NewsDatabase
import com.example.znews.database.NewsOneModel

class NewsOneRepository(
    context: Context
) {
    private val newsOneDao: NewOneDao

    init {
        val database = NewsDatabase.getDatabase(context)
        newsOneDao = database.newsOneDao()
    }

    suspend fun insertNews(newsList: List<NewsOneModel>) {
        for (news in newsList) {
            newsOneDao.insertNews(news)
            Log.d("NewsOneRepository", "Inserted news: ${news.articleId}")
        }
    }

    fun getAllNews(): LiveData<List<NewsOneModel>> {
        return newsOneDao.getAllNews()
    }

    fun getAllNewsList(): List<NewsOneModel> {
        return newsOneDao.getAllNewsList()
    }

    suspend fun deleteAllNews() {
        newsOneDao.resetTable()
        newsOneDao.deleteAllNews()
    }

    fun getNewsByCategory(category: String): LiveData<List<NewsOneModel>> {
        return newsOneDao.getNewsByCategory(category)
    }



}
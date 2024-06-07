package com.example.newswithweather.repository

import android.content.Context
import android.util.Log
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

    suspend fun insertNewNews(newsList: List<NewsModel>) {
        for (news in newsList) {
            val existingNews = newsDao.getNewsByDateTime(news.date, news.time)
            if (existingNews == null || existingNews.title != news.title) {
                newsDao.insertNews(news)
            }
        }
    }

    fun getAllNews(): LiveData<List<NewsModel>>{
        return newsDao.getAllNews()
    }

    suspend fun deleteAllNews(){
        newsDao.resetTable()
        newsDao.deleteAllNews()
    }

    fun getNewsByCategory(category: String): LiveData<List<NewsModel>> {
        return newsDao.getNewsByCategory(category)
    }

    fun getPaginatedNews(page: Int, pageSize: Int, category: String): LiveData<List<NewsModel>> {
        val offset = (page - 1) * pageSize
        Log.i("NewsRepository -> getPaginatedNews", "offset = $offset page = $page pageSize = $pageSize")
        val result = newsDao.getPaginatedNews(offset, pageSize, category)
        return result
    }

    fun filterNews(query: String?): LiveData<List<NewsModel>> {
        if (query.isNullOrEmpty()) {
            return newsDao.getAllNews()
        }
        return newsDao.filterNews(query)
    }

    fun getPaginatedAll(page: Int, pageSize: Int): LiveData<List<NewsModel>> {
        val offset = (page - 1) * pageSize
        return newsDao.getPaginatedAll(offset, pageSize)
    }
}
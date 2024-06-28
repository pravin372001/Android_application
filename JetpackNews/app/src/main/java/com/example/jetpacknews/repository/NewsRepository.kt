package com.example.jetpacknews.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jetpacknews.database.NewsDao
import com.example.jetpacknews.database.NewsDatabase
import com.example.jetpacknews.database.NewsModel
import com.example.jetpacknews.viewmodel.NewsViewModel

class NewsRepository(context: Context) {
    private val newsDao : NewsDao

    init {
        val database = NewsDatabase.getDatabase(context)
        newsDao = database.newsDao()
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

    fun filterNews(query: String): LiveData<List<NewsModel>> {
        Log.i("NewsRepository -> filterNews", "query = $query")
        return newsDao.filterNews(query)
    }

    fun getPaginatedAll(page: Int, pageSize: Int): LiveData<List<NewsModel>> {
        val offset = (page - 1) * pageSize
        return newsDao.getPaginatedAll(offset, pageSize)
    }
}
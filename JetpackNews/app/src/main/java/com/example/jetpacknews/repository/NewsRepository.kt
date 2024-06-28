package com.example.jetpacknews.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.jetpacknews.database.NewsDao
import com.example.jetpacknews.database.NewsDatabase
import com.example.jetpacknews.database.NewsModel
import com.example.jetpacknews.viewmodel.NewsViewModel
import kotlinx.coroutines.flow.Flow

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

    fun getPaginatedByCategory(category: String) : Flow<PagingData<NewsModel>> {
        return Pager(config = PagingConfig(pageSize = 5, enablePlaceholders = false)) {
            newsDao.getPagingNewsByCategory(category)
        }.flow
    }

    fun getPagingAllNews(): Flow<PagingData<NewsModel>> {
        return Pager(config = PagingConfig(pageSize = 5, enablePlaceholders = false)) {
            newsDao.getPagingAllNews()
        }.flow
    }

    fun getFilteredPagingNews(query: String): Flow<PagingData<NewsModel>> {
        return Pager(config = PagingConfig(pageSize = 5, enablePlaceholders = false)) {
            newsDao.getPagingFilteredNews(query)
        }.flow
    }
}
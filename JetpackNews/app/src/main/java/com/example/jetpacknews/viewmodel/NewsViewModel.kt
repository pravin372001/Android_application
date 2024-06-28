package com.example.jetpacknews.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.jetpacknews.api.NewsApi
import com.example.jetpacknews.database.NewsModel
import com.example.jetpacknews.model.news.Data
import com.example.jetpacknews.repository.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel(private val repository: NewsRepository): ViewModel() {

    private val isDbDataInserted = MutableLiveData<Boolean>(false)
    val isDbDataInsertedLiveData: LiveData<Boolean> = isDbDataInserted

    private var _newsList = MutableLiveData<List<NewsModel>>()
    val newsList: LiveData<List<NewsModel>> get() = _newsList

    fun getCatergoriesList() : List<String> = arrayListOf("all","national", "business",
        "sports", "world",
        "politics", "technology", "startup", "entertainment",
        "miscellaneous", "hatke", "science", "automobile")

    fun fetchNews(category: String) {
        viewModelScope.launch {
            try {
                isDbDataInserted.value = false
                val newsData = withContext(Dispatchers.IO) {
                    NewsApi.retrofitService.getNews(category)
                }
                Log.i("NewsViewModel - fetchNews", "$newsData")
                formatNews(newsData.data, newsData.category, newsData.success)
            } catch (e: Exception) {
                Log.i("NewsViewModel - Error", "${e.stackTrace}")
            } finally {
                isDbDataInserted.value = true
            }
        }
    }

    private fun formatNews(newsList: List<Data>, category: String, success: Boolean) {
        val newsModels = newsList.map { news ->
            NewsModel(
                category = category,
                author = news.author,
                content = news.content,
                date = news.date,
                id = news.id,
                imageUrl = news.imageUrl,
                readMoreUrl = news.readMoreUrl,
                time = news.time,
                title = news.title,
                url = news.url,
                success = success
            )
        }
        viewModelScope.launch {
            repository.insertNewNews(newsModels)
        }
    }

    fun deleteNews() {
        viewModelScope.launch {
            // Execute delete operation on a background thread
            repository.deleteAllNews()
        }
    }

    fun filterNews(query: String){
        viewModelScope.launch {
            val filteredNews = withContext(Dispatchers.IO) {
                repository.filterNews(query)
            }
            filteredNews.observeForever {
                _newsList.value = it
            }
        }
    }

    fun getNews(category: String) {
        viewModelScope.launch {
            try {
                val news = withContext(Dispatchers.IO) {
                    if (category == "all") {
                        repository.getAllNews()
                    } else {
                        repository.getNewsByCategory(category)
                    }
                }
                news.observeForever {
                    _newsList.value = it
                    Log.i("NewsViewModel - getNews", it.toString())
                }
            } catch (e: Exception) {
                Log.i("NewsViewModel - Error", "${e.stackTrace}")
            }
        }
    }

    fun onCategorySelected(text: String) {
        Log.i("NewsViewModel - onCategorySelected", text)
        getNews(text.lowercase())
        fetchPagingNews(text.lowercase())
    }

    private var _pagingNewsList = MutableStateFlow<PagingData<NewsModel>>(PagingData.empty())
    val pagingNewsList = _pagingNewsList.asStateFlow()

    fun fetchPagingNews(category: String) {
        viewModelScope.launch {
            val flow = if (category == "all") {
                repository.getPagingAllNews()
            } else {
                repository.getPaginatedByCategory(category)
            }

            flow.cachedIn(viewModelScope).collectLatest {
                _pagingNewsList.value = it
                Log.i("NewsViewModel - fetchPagingNews", it.toString())
            }
        }
    }

    fun getFilteredPagingNews(query: String) {
        viewModelScope.launch {
            val flow = repository.getFilteredPagingNews(query)
            flow.cachedIn(viewModelScope).collectLatest {
                _pagingNewsList.value = it
            }
        }
    }

    class NewsViewModelFactory(private val repository: NewsRepository): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(NewsViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return NewsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
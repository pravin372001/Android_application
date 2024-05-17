package com.example.newswithweather.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.newswithweather.database.NewsModel
import com.example.newswithweather.model.news.News
import com.example.newswithweather.repository.NewsRepository
import com.example.newswithweather.service.NewsApi
import com.example.newswithweather.service.NewsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class NewsViewModel(private val repository:NewsRepository): ViewModel() {

    private val isDbDataInserted = MutableLiveData<Boolean>()
    val isDbDataInsertedLiveData: LiveData<Boolean> = isDbDataInserted
    // Define a MutableLiveData to observe deletion progress
    private val deletionInProgressLiveData = MutableLiveData<Boolean>()

    private val _newsListPage = MutableLiveData<List<NewsModel>>()
    val newsListPage: LiveData<List<NewsModel>> get() = _newsListPage

    private val _newsListTemp = MutableLiveData<MutableList<NewsModel>>(mutableListOf())
    val newsListTemp: LiveData<MutableList<NewsModel>> = _newsListTemp


    private val _newsList = MutableLiveData<MutableList<NewsModel>>()
    val newsList: LiveData<MutableList<NewsModel>> = _newsList

    private val pageSize = 4 // Number of items per page
    private var currentPage = 1
    private val _currentCategory = MutableLiveData<String>("all")
    val currentCategory: LiveData<String> = _currentCategory

    init {
        fetchNews()
    }

    fun setCategory(category: String) {
        _currentCategory.value = category
        resetPage()
        fetchNews()
    }

    fun incrementPage() {
        currentPage++
        fetchNews()
    }

    fun resetPage() {
        currentPage = 1
        _newsListTemp.value = mutableListOf()  // Reset the news list when the category changes
    }

    fun fetchNews(category: String){
        val news = MutableLiveData<News>()
        viewModelScope.launch {
            try {
                isDbDataInserted.value = false
                val newsData = withContext(Dispatchers.IO){
                    NewsApi.retrofitService.getNews(category)
                }
                Log.i("NewsViewModel - fetchNews", "${newsData}")
                news.value = newsData
                formatNews(news)
                isDbDataInserted.value = true
            } catch (e:Exception){
                Log.i("NewsViewModel - Error", "${e.stackTrace}")
            }
        }
    }

    private fun formatNews(news : MutableLiveData<News>){
        for(i in 0 until news.value!!.data.size){
            val newsModel = NewsModel(category = news.value!!.category, author =  news.value!!.data[i].author, content =  news.value!!.data[i].content, date =  news.value!!.data[i].date, id =  news.value!!.data[i].id
                , imageUrl =  news.value!!.data[i].imageUrl, readMoreUrl =  news.value!!.data[i].readMoreUrl, time =  news.value!!.data[i].time,title = news.value!!.data[i].title,url = news.value!!.data[i].url,
                success = news.value!!.success
            )
            _newsList.value?.set(i, newsModel)
            Log.i("NewsViewModel", "${newsModel}")
            insertNews(newsModel)
        }
    }

    private fun insertNews(newsModel: NewsModel){
        viewModelScope.launch {
            repository.insertNews(newsModel)
        }
    }

    fun deleteNews() {
        viewModelScope.launch {
            // Execute delete operation on a background thread
            repository.deleteAllNews()
        }
    }


    fun getNewsByCategory(category: String): LiveData<List<NewsModel>> {
        return repository.getNewsByCategory(category)
    }
    fun getAllNews() : LiveData<List<NewsModel>>{
        return repository.getAllNews()
    }


    fun filterNews(query: String?): LiveData<List<NewsModel>> {
        return repository.filterNews(query)
    }

    fun fetchNews() {
        viewModelScope.launch {
            val category = _currentCategory.value ?: "all"
            val page = currentPage
            try {
                Log.i("NewsViewModel", "Fetching news for category: $category, page: $page")
                val newsListLiveData = withContext(Dispatchers.IO) {
                    if (category == "all") {
                        repository.getPaginatedAll(page, pageSize)
                    } else {
                        repository.getPaginatedNews(page, pageSize, category)
                    }
                }
                newsListLiveData.observeForever { newsList ->
                    Log.i("NewsViewModel", "Fetched news: $newsList")
                    appendNews(newsList)
                }
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Error fetching news", e)
                _newsListPage.postValue(emptyList())
            }
        }
    }

    private fun appendNews(newsList: List<NewsModel>) {
        val currentList = _newsListTemp.value ?: mutableListOf()
        currentList.addAll(newsList.filterNot { currentList.contains(it) })  // Avoid duplicates
        _newsListTemp.value = currentList
        _newsListPage.postValue(currentList)
    }


    fun getCurrentPage(): Int {
        return currentPage
    }

    class NewsViewModelFactory(private val repository: NewsRepository):ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(NewsViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return NewsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
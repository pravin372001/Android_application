package com.example.newswithweather.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
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

    private val _newsList = MutableLiveData<MutableList<NewsModel>>()
    val newsList: LiveData<MutableList<NewsModel>> = _newsList
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

    // Define a MutableLiveData to observe deletion progress
    private val deletionInProgressLiveData = MutableLiveData<Boolean>()

    // Function to start deletion process
//    fun deleteNews() {
//        viewModelScope.launch {
//            deletionInProgressLiveData.value = true
//
//            // Execute delete operation on a background thread
//            repository.deleteAllNews()
//
//            // After deletion is completed, update LiveData to indicate completion
//            deletionInProgressLiveData.value = false
//        }
//    }

    fun getNewsByCategory(category: String): LiveData<List<NewsModel>> {
        return repository.getNewsByCategory(category)
    }
    fun getAllNews() : LiveData<List<NewsModel>>{
        return repository.getAllNews()
    }


    fun filterNews(query: String?): LiveData<List<NewsModel>> {
        return repository.filterNews(query)
    }

    private val pageSize = 4 // Number of items per page

    private var _newsListPage :LiveData<List<NewsModel>> = MutableLiveData<List<NewsModel>>()
    val newsListPage: LiveData<List<NewsModel>> = _newsListPage

    fun fetchNews(page: Int = 1, category: String): LiveData<List<NewsModel>> {
        var newsData: LiveData<List<NewsModel>> = MutableLiveData<List<NewsModel>>()
        if(category == "all"){
            newsData = repository.getPaginatedAll(page, pageSize)
        } else {
            newsData = repository.getPaginatedNews(page, pageSize, category)
        }
        return newsData
    }

    // Function to fetch news
//    fun fetchNews(page: Int = 1, category: String): LiveData<List<NewsModel>> {
//        val newsData: LiveData<List<NewsModel>> = if (category == "all") {
//            repository.getPaginatedAll(page, pageSize)
//        } else {
//            repository.getPaginatedNews(page, pageSize, category)
//        }
//
//        // Create a new LiveData to observe the deletion progress
//        val combinedLiveData = MediatorLiveData<Pair<Boolean, List<NewsModel>>>()
//        combinedLiveData.addSource(deletionInProgressLiveData) { deletionInProgress ->
//            if (!deletionInProgress) {
//                combinedLiveData.value = Pair(deletionInProgress, newsData.value ?: emptyList())
//            }
//        }
//        combinedLiveData.addSource(newsData) { news ->
//            val deletionInProgress = deletionInProgressLiveData.value ?: false
//            combinedLiveData.value = Pair(deletionInProgress, news)
//        }
//
//        return combinedLiveData.map { it.second }
//    }

//    suspend fun fetchNews(page: Int = 1, category: String): LiveData<List<NewsModel>> {
//        return withContext(Dispatchers.Default) {
//            // Acquire the lock to check if deletion is in progress
//            deleteMutex.withLock {
//                if (isDeleteInProgress) {
//                    // If deletion is in progress, suspend until it finishes
//                    while (isDeleteInProgress) {
//                        delay(100) // Check every 100 milliseconds
//                    }
//                }
//
//                // Perform the data fetching operation after deletion is finished
//                val newsData: LiveData<List<NewsModel>> = if (category == "all") {
//                    repository.getPaginatedAll(page, pageSize)
//                } else {
//                    repository.getPaginatedNews(page, pageSize, category)
//                }
//                newsData
//            }
//        }
//    }

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
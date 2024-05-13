package com.example.newswithweather.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.newswithweather.database.NewsModel
import com.example.newswithweather.model.news.News
import com.example.newswithweather.repository.NewsRepository
import com.example.newswithweather.service.NewsApi
import com.example.newswithweather.service.NewsApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel(private val repository:NewsRepository): ViewModel() {

//    private var _newsListPage : LiveData<List<NewsModel>> = MutableLiveData<List<NewsModel>>()
//    val newsListPage: LiveData<List<NewsModel>> = _newsListPage
    fun fetchNews(category: String){
        val news = MutableLiveData<News>()
        viewModelScope.launch {
            try {
                val newsData = withContext(Dispatchers.IO){
                    NewsApi.retrofitService.getNews(category)
                }
                Log.i("NewsViewModel - fetchNews", "${newsData}")
                news.value = newsData
                formatNews(news)
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

    fun deleteNews(){
        viewModelScope.launch {
            repository.deleteAllNews()
        }
    }

    fun getNewsByCategory(category: String): LiveData<List<NewsModel>> {
        return repository.getNewsByCategory(category)
    }
    fun getAllNews() : LiveData<List<NewsModel>>{
        return repository.getAllNews()
    }

    suspend fun reset() {
        viewModelScope.launch {
            repository.reset()
        }
    }

    fun filterNews(query: String?): LiveData<List<NewsModel>> {
        return repository.filterNews(query)
    }

    fun fetchNews(page: Int, category: String): LiveData<List<NewsModel>> {
        return repository.getPaginatedNews(page, PAGE_SIZE, category)
    }

    companion object {
        private const val PAGE_SIZE = 10
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
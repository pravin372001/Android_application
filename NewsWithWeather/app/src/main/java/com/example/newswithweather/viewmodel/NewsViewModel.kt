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
import com.example.newswithweather.database.Converters
import com.example.newswithweather.database.NewsModel
import com.example.newswithweather.model.news.Data
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class NewsViewModel(private val repository:NewsRepository): ViewModel() {

    private val isDbDataInserted = MutableLiveData<Boolean>()
    val isDbDataInsertedLiveData: LiveData<Boolean> = isDbDataInserted
    // Define a MutableLiveData to observe deletion progress
    private val deletionInProgressLiveData = MutableLiveData<Boolean>()

    private var selectPosition = 0

    private val _newsListPage = MutableLiveData<List<NewsModel>>()
    val newsListPage: LiveData<List<NewsModel>> get() = _newsListPage

    private val _newsListTemp = MutableLiveData<MutableList<NewsModel>>(mutableListOf())

    private val _newsList = MutableLiveData<MutableList<NewsModel>>()

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

    fun setSelectPosition(position: Int) {
        selectPosition = position
    }

    fun getSelectPosition(): Int{
        return selectPosition
    }

    fun setDNInserted(value: Boolean) {
        isDbDataInserted.value = value
    }

    fun incrementPage() {
        currentPage++
        fetchNews()
    }

    fun resetPage() {
        currentPage = 1
        _newsListTemp.value = mutableListOf()  // Reset the news list when the category changes
    }

    fun getCategories(): List<String>{
        val apiCatagoryList: ArrayList<String> = arrayListOf("all","national", "business",
            "sports", "world",
            "politics", "technology", "startup", "entertainment",
            "miscellaneous", "hatke", "science", "automobile")
        return apiCatagoryList
    }

    fun fetchNews(category: String) {
        viewModelScope.launch {
            try {
                isDbDataInserted.value = false
                val newsData = withContext(Dispatchers.IO) {
                    NewsApi.retrofitService.getNews(category)
                }
                Log.i("NewsViewModel - fetchNews", "$newsData")
                formatNews(newsData.data, newsData.category, newsData.success)
                isDbDataInserted.value = true
            } catch (e: Exception) {
                Log.i("NewsViewModel - Error", "${e.stackTrace}")
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

    private fun sortNewsByDateAndTime(newsList: List<NewsModel>): List<NewsModel> {
        val dateFormat = SimpleDateFormat("EEEE, dd MMM, yyyy hh:mm a", Locale.getDefault())
        val comparator = Comparator<NewsModel> { news1, news2 ->
            try {
                // Parse the date strings into Date objects
                val date1 = dateFormat.parse("${news1.date} ${news1.time}")
                val date2 = dateFormat.parse("${news2.date} ${news2.time}")
                Log.i("NewsViewModel -> Comparator", "date1 = $date1 date2 = $date2")
                // Compare the Date objects
                date2.compareTo(date1) // Reverse order to sort from newest to oldest
            } catch (e: ParseException) {
                e.printStackTrace()
                0 // Return 0 in case of parsing error, maintaining original order
            }
        }

        // Sort the newsList using the comparator
        val sortedList = ArrayList(newsList)
        sortedList.sortWith(comparator)

        return sortedList
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
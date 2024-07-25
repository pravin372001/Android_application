package com.example.znews.viewmodel

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.window.layout.FoldingFeature
import com.example.newswithweather.model.news.Data
import com.example.znews.R
import com.example.znews.adapter.NewsAdapter
import com.example.znews.database.NewsModel
import com.example.znews.database.NewsOneModel
import com.example.znews.model.newsone.Result
import com.example.znews.remote.NewsApi
import com.example.znews.remote.NewsOneApi
import com.example.znews.repository.NewsOneRepository
import com.example.znews.repository.NewsRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModel(
    private val googleSignInClient: GoogleSignInClient,
    private val context: Context,
    private val repository: NewsRepository,
    private val newsOneRepository: NewsOneRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val currentPage = MutableLiveData<Int>()

    private val isDbDataInserted = MutableLiveData<Boolean>()
    val isDbDataInsertedLiveData: LiveData<Boolean> = isDbDataInserted

    private val isDbDataInsertedNewsOne = MutableLiveData<Boolean>()
    val isDbDataInsertedNewsOneLiveData: LiveData<Boolean> = isDbDataInsertedNewsOne

    private var selectPosition = 0

    private var _currentNews = MutableLiveData<NewsModel>()
    val currentNews: LiveData<NewsModel> = _currentNews

    private val _newsListPage = MutableLiveData<List<NewsModel>>()
    val newsListPage: LiveData<List<NewsModel>> get() = _newsListPage

    private val _newsOneListPage = MutableLiveData<List<NewsOneModel>>()
    val newsOneListPage: LiveData<List<NewsOneModel>> get() = _newsOneListPage

    private val _foldState = MutableLiveData<FoldingFeature?>()
    val foldState: LiveData<FoldingFeature?> get() = _foldState

    init {
        auth.addAuthStateListener { auth ->
            _user.value = auth.currentUser
            Log.d(TAG, "Auth state changed: ${auth.currentUser?.uid}")
        }
        fetchNewsOne()
        if(isNetworkOn()) {
            deleteNews()
            for (category in getCategories()) {
                fetchNews(category)
                setDbDataInserted(true)
            }
        } else {
            setDbDataInserted(true)
        }
    }

    fun setCurrentNews(news: NewsModel){
        _currentNews.value = news
    }

    fun selectPosition(position: Int) {
        Log.d(TAG, "Select position: $position")
        selectPosition = position
    }

    fun getSelectPosition(): Int {
        Log.d(TAG, "Select position: $selectPosition")
        return selectPosition
    }

    fun login(idToken: String){
        viewModelScope.launch {
            signInWithGoogle(idToken)
        }
    }

    private suspend fun signInWithGoogle(idToken: String): Boolean {
        if (context !is Activity) {
            throw IllegalArgumentException("Context must be an Activity")
        }
        var success: Boolean = false
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(idToken)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        withContext(Dispatchers.Main) {
            try {
                val result = credentialManager.getCredential(request = request, context = context)
                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential
                    .createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnSuccessListener {
                        Log.d(TAG, "signInWithGoogle: Success")
                        _user.value = auth.currentUser
                        success = true
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG, "signInWithGoogle: Failure", e)
                    }

            } catch (e: Exception) {
                credentialManager.clearCredentialState(
                    ClearCredentialStateRequest()
                )
                Log.e(TAG, "signInWithGoogle: Exception", e)
            }
        }
        delay(1000)
        return success
    }

    fun logout() {
        auth.signOut()
        googleSignInClient.signOut()
        _user.value = null
    }

    fun getCategories(): List<String>{
        val apiCatagoryList: ArrayList<String> = arrayListOf("all","national", "business",
            "sports", "world",
            "politics", "technology", "startup", "entertainment",
            "miscellaneous", "hatke", "science", "automobile")
        return apiCatagoryList
    }

    private fun fetchNews(category: String) {
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
            repository.insertNews(newsModels)
        }
    }

    private fun fetchNewsOne() {
        isDbDataInsertedNewsOne.value = false
        viewModelScope.launch {
            try {
                val newsData = withContext(Dispatchers.IO) {
                    NewsOneApi.retrofitService.getNews(API)
                }
                Log.i("NewsViewModel - fetchNewsOne", "$newsData")
//                currentPage.value = newsData.totalResults
//                fetchNewsOneByPage()
                formatNewsOne(newsData.results)
            } catch (e:Exception) {
                Log.i("NewsViewModel - fetchNewsOne Error", e.message.toString())
                Log.e(TAG, "fetchNewsOne: Exception", e.cause)
            }
        }
    }

    private fun fetchNewsOneByPage() {
        for(i in 0..9) {
            viewModelScope.launch {
                try {
                    val newsData = withContext(Dispatchers.IO) {
                        NewsOneApi.retrofitService.getNewsByPageNumber(API, currentPage.value!!)
                    }
                    Log.i("NewsViewModel - fetchNewsOne", "$newsData")
                    formatNewsOne(newsData.results)
                } catch (e: Exception) {
                    Log.i("NewsViewModel - Error", e.message.toString())
                }
            }
        }
    }

    private fun formatNewsOne(results: List<Result>) {
        val newsModels = results.map {
            NewsOneModel(
                title = it.title ?: "",
                category = it.category?.firstOrNull() ?: "", // Ensure category is not null
                content = it.content ?: "",
                country = it.country?.toString() ?: "",
                creator = it.creator?.firstOrNull() ?: "", // Ensure creator list is not null
                description = it.description ?: "",
                image_url = it.image_url ?: "",
                pubDate = it.pubDate ?: "",
                link = it.link ?: ""
            )
        }
        viewModelScope.launch {
            newsOneRepository.deleteAllNews()
            newsOneRepository.insertNews(newsModels)
            isDbDataInsertedNewsOne.value = true
        }
    }

    fun fetchNewsOneFromDb() {
        viewModelScope.launch {
            val newsList = withContext(Dispatchers.IO) {
                newsOneRepository.getAllNews()
            }
            newsList.observeForever {
                _newsOneListPage.value = it
            }
        }
        Log.d(TAG, "News list size: ${newsOneListPage.toString()}")
    }

    fun fetchFromDb(category: String) {
        Log.d(TAG, "Category: $category")
        viewModelScope.launch {
            val newsList = withContext(Dispatchers.IO) {
                repository.getNewsByCategory(category)
            }
            newsList.observeForever {
                _newsListPage.value = it
            }
        }
    }

    fun currentUser(): Boolean{
        Log.d(TAG, "User signed in: ${auth.currentUser?.displayName}")
        return auth.currentUser != null
    }

    fun deleteNews() {
        viewModelScope.launch {
            repository.deleteAllNews()
        }
    }

    fun setDbDataInserted(value: Boolean) {
        isDbDataInserted.value = value
    }

    companion object {
        private const val API = "pub_493099aad351d0663cebc67551d7d81abba60"
        private const val TAG = "NewsViewModel"
        const val RC_SIGN_IN = 9001
    }

    fun isNetworkOn() : Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun setFoldState(foldState: FoldingFeature?) {
        _foldState.value = foldState
        Log.d(TAG, "Fold state: $foldState")
    }
}

class NewsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(googleSignInClient, context, NewsRepository(context), NewsOneRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
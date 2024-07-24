package com.example.znews.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.example.newswithweather.model.news.Data
import com.example.znews.R
import com.example.znews.adapter.NewsAdapter
import com.example.znews.database.NewsModel
import com.example.znews.remote.NewsApi
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
    private val repository: NewsRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val isDbDataInserted = MutableLiveData<Boolean>()
    val isDbDataInsertedLiveData: LiveData<Boolean> = isDbDataInserted

    private var selectPosition = 0

    private var _currentNews = MutableLiveData<NewsModel>()
    val currentNews: LiveData<NewsModel> = _currentNews

    private val _newsListPage = MutableLiveData<List<NewsModel>>()
    val newsListPage: LiveData<List<NewsModel>> get() = _newsListPage

    init {
        auth.addAuthStateListener { auth ->
            _user.value = auth.currentUser
            Log.d(TAG, "Auth state changed: ${auth.currentUser?.uid}")
        }
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
        private const val TAG = "NewsViewModel"
        const val RC_SIGN_IN = 9001
    }

    fun isNetworkOn() : Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
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
            return NewsViewModel(googleSignInClient, context, NewsRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
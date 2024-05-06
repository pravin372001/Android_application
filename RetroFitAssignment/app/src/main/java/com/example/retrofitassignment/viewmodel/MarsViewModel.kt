package com.example.retrofitassignment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofitassignment.model.MarsPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MarsViewModel : ViewModel(){
    private var listResult: List<MarsPhoto>
    init {
        listResult = ArrayList()
    }
    fun getMarsPhotos(): List<MarsPhoto> {
        viewModelScope.launch {
            listResult = fetchMarsPhotos()
        }
        return listResult
    }

    private suspend fun fetchMarsPhotos(): List<MarsPhoto> {
        return withContext(Dispatchers.IO) {
            MarsApi.retrofitService.getPhotos()
        }
    }
}
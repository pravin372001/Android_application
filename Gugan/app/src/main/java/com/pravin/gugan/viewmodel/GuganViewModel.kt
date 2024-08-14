package com.pravin.gugan.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pravin.gugan.database.ImageEntity
import com.pravin.gugan.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GuganViewModel(private val repository: ImageRepository): ViewModel() {

    private val _images = MutableLiveData<ImageEntity>()
    val images: LiveData<ImageEntity> = _images

    init {
        getImage()
    }

    fun insertImage(imageEntity: ImageEntity) {
        viewModelScope.launch {
            repository.insertImage(imageEntity)
        }
    }

    private fun getImage() {
        viewModelScope.launch {
            val images = withContext(Dispatchers.IO) {
                repository.getAllImages()
            }
            images.observeForever {
                _images.value = it
            }
        }
    }

    suspend fun deleteAllImages() {
        repository.deleteAllImages()
    }
}
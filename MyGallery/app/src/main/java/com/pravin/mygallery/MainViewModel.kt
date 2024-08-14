package com.pravin.mygallery

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _images = MutableLiveData<List<Uri>>()
    val images: LiveData<List<Uri>> get() = _images

    private val _uiState = mutableStateOf<UiState>(UiState.Loading)
    val uiState: UiState get() = _uiState.value

    private val selectedImageUri = mutableStateOf<Uri?>(null)
    val selectedImage: Uri? get() = selectedImageUri.value

    fun setSelectedImage(uri: Uri?) {
        selectedImageUri.value = uri
    }

    init {
        loadImages()
    }

    private fun loadImages() {
        viewModelScope.launch {
            val imageUris = withContext(Dispatchers.IO) {
                val resolver: ContentResolver = getApplication<Application>().contentResolver
                val projection = arrayOf(MediaStore.Images.Media._ID)
                val cursor = resolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
                )
                val uris = mutableListOf<Uri>()
                cursor?.use {
                    val idIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    while (it.moveToNext()) {
                        val id = it.getLong(idIndex)
                        uris.add(Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString()))
                    }
                }
                uris
            }
            Log.d("MainViewModel", "Image URIs: ${imageUris.toString()}")
            _images.postValue(imageUris)
            _uiState.value = UiState.Success
        }
    }
}

sealed class UiState{
    data object Loading : UiState()
    data object Success : UiState()
}

package com.example.mediagallery

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _photos = MutableStateFlow<List<String>>(emptyList())
    val photos: StateFlow<List<String>> = _photos

    fun loadPhotos(contentResolver: ContentResolver) {
        viewModelScope.launch {
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
            val cursor: Cursor? = contentResolver.query(
                uri, projection, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC"
            )

            cursor?.use {
                val columnIndexData = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val photoPaths = mutableListOf<String>()
                while (it.moveToNext()) {
                    val absolutePathOfImage = it.getString(columnIndexData)
                    photoPaths.add(absolutePathOfImage)
                }
                _photos.value = photoPaths
            }
        }
    }
}

package com.example.roomdatabaseassignment.viewmodel

// DataViewModelFactory.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.roomdatabaseassignment.database.DataDao


class DataViewModelFactory(private val dataDao: DataDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DataViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DataViewModel(dataDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

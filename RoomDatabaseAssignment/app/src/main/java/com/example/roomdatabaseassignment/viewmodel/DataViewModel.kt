package com.example.roomdatabaseassignment.viewmodel

// DataViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdatabaseassignment.database.Data
import com.example.roomdatabaseassignment.database.DataDao
import kotlinx.coroutines.launch

class DataViewModel(private val dataDao: DataDao) : ViewModel() {

    fun addData(dataEntity: Data) {
        viewModelScope.launch {
            dataDao.insertData(dataEntity)
        }
    }

    fun getAllData(): List<Data> {
        return dataDao.getAllData()
    }

    fun deleteData() {
        viewModelScope.launch {
            dataDao.deleteData()
        }
    }
}


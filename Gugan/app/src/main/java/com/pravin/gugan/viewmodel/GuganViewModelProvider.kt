package com.pravin.gugan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pravin.gugan.repository.ImageRepository

class GuganViewModelProvider(private val repository: ImageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GuganViewModel(repository) as T
    }
}
package com.example.thirukkural

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ThirukkuralViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThirukkuralViewModel::class.java)) {

            return ThirukkuralViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.example.retrofitrecycler

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofitrecycler.model.CountryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CountryViewModel: ViewModel() {
    private val _countryList = MutableLiveData<List<CountryItem>>()
    val countryList: LiveData<List<CountryItem>> = _countryList

    init {
        fetchCountry()
    }

    private fun fetchCountry() {
        viewModelScope.launch {
            try {
                val countryData = withContext(Dispatchers.IO) {
                    CountryApi.retrofitApiService.getCountry()
                }
                _countryList.value = countryData
            } catch (e: Exception) {
                Log.i("CountryViewModel", "${e.stackTrace}")
            }
        }
    }
}
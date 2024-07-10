package com.pravin.mycontactview.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pravin.mycontactview.remote.ContactApi
import com.pravin.mycontactview.remote.model.Result
import com.pravin.mycontactview.remote.model.Contact
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    sealed class ContactUiState {
        data object Success: ContactUiState()
        data object Error: ContactUiState()
        data object Loading: ContactUiState()
    }

    sealed class TopUiState{
        data object Enabled: TopUiState()
        data object Disabled: TopUiState()
    }

    var topUiState: TopUiState by mutableStateOf(TopUiState.Disabled)
        private set


    var contactUiState: ContactUiState by mutableStateOf(ContactUiState.Loading)
        private set

    private val _contacts = MutableLiveData<Contact>()
    val contacts: LiveData<Contact> get() = _contacts

    private var _result = MutableLiveData<Result?>()
    val result: LiveData<Result?> get() = _result

    init {
        getContacts()
    }

    fun onTopChange(state: TopUiState){
        topUiState = state
    }

    fun onContactClicked(result: Result){
        _result.value = result
        Log.d("ContactViewModel", "onContactClicked: ${_result.value.toString()}")
    }

    fun onBackClicked(){
        _result.value = null
        Log.d("ContactViewModel", "onBackClicked: ${_result.value}")
    }

    fun getContacts() {
        viewModelScope.launch {
            try {
                contactUiState = ContactUiState.Loading
                _contacts.value = ContactApi.retrofitService.getContacts()
                contactUiState = ContactUiState.Success
                getSorted()
            } catch (e: Exception) {
                Log.e("ContactViewModel", "Error fetching contacts", e)
                contactUiState = ContactUiState.Error
            }
        }
    }

    private fun getSorted(){
        _contacts.value?.results?.sortedBy { it.name.first }
    }


    class ContactViewModelFactory(
        private val application: Application
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ContactViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}

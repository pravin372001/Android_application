package com.pravin.mycontactgenieviews.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.window.layout.FoldingFeature
import com.pravin.mycontactgenieviews.remote.ContactApi
import com.pravin.mycontactgenieviews.remote.model.Contact
import com.pravin.mycontactgenieviews.remote.model.Result
import kotlinx.coroutines.launch

class ContactViewModel(application: Application): AndroidViewModel(application) {
    sealed class ContactUiState {
        data object Success: ContactUiState()
        data object Error: ContactUiState()
        data object Loading: ContactUiState()
    }

    private val _uiState = MutableLiveData<ContactUiState>(ContactUiState.Loading)
    val uiState: LiveData<ContactUiState> = _uiState

    private val _contacts = MutableLiveData<Contact>()
    val contacts: LiveData<Contact> get() = _contacts

    private val _currentContact = MutableLiveData<Result?>()
    val currentContact: LiveData<Result?> get() = _currentContact

    private val _foldState = MutableLiveData<FoldingFeature?>()
    val foldState: LiveData<FoldingFeature?> get() = _foldState

    fun setCurrentContact(contact: Result?) {
        _currentContact.value = contact
        Log.d("ContactViewModel", "Current contact set to $contact")
    }

    fun setFoldState(foldState: FoldingFeature?) {
        _foldState.value = foldState
        Log.d("ContactViewModel", "Fold state set to $foldState")
    }


    init {
        getContacts()
    }

    fun getContacts() {
        viewModelScope.launch {
            try {
                _uiState.value = ContactUiState.Loading
                _contacts.value = ContactApi.retrofitService.getContacts()
                _uiState.value = ContactUiState.Success
            } catch (e: Exception) {
                Log.e("ContactViewModel", "Error fetching contacts", e)
                _uiState.value = ContactUiState.Error
            }
        }
    }

}
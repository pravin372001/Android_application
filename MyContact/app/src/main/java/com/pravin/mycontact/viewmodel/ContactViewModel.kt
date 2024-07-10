package com.pravin.mycontact.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pravin.mycontact.remote.ContactApi
import com.pravin.mycontact.remote.model.Contact
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private val _contacts = MutableLiveData<Contact>()
    val contacts: LiveData<Contact> get() = _contacts

    init {
        getContacts()
    }

    private fun getContacts() {
        viewModelScope.launch {
            try {
                _contacts.value = ContactApi.retrofitService.getContacts()
            } catch (e: Exception) {
                Log.e("ContactViewModel", "Error fetching contacts", e)
            }
        }
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

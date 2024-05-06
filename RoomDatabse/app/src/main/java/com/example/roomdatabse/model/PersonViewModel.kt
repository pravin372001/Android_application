package com.example.roomdatabse.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roomdatabse.database.Person
import com.example.roomdatabse.database.PersonRepository
import kotlinx.coroutines.launch

class PersonViewModel(private val repository: PersonRepository) : ViewModel() {

    // No need for MutableLiveData here, Room handles LiveData internally
    // Use regular variables to hold the values
    var personName: String = ""
    var personAge: Int = 0
    var personPhone: String = ""
    var personEmail: String = ""


    var lastInsertedPerson: Person? = null

    // Function to add a person to the database
    fun addPersonFromInput() {
        val person = Person(name = personName, age = personAge, phone = personPhone, email = personEmail)
        viewModelScope.launch {
            repository.insertPerson(person)
            lastInsertedPerson = person
            Log.i("PersonViewModel", "age = ${lastInsertedPerson?.age} , name = ${lastInsertedPerson?.name}")
        }
    }

    // Function to delete a person from the database
    fun deletePerson() {
        viewModelScope.launch {
            repository.deletePerson()
        }
    }

    fun getPerson(): LiveData<Person?> {
        return repository.getPerson()
    }
}


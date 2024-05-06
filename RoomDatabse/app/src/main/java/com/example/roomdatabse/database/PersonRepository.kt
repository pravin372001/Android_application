package com.example.roomdatabse.database

import android.content.Context
import androidx.lifecycle.LiveData

class PersonRepository(context: Context) {

    private val personDao: PersonDao

    init {
        val database = PersonDatabase.getDatabase(context)
        personDao = database.personDao()
    }

    // Function to insert a person into the database
    suspend fun insertPerson(person: Person) {
        personDao.insert(person)
    }

    // Function to delete a person from the database
    suspend fun deletePerson() {
        personDao.delete()
    }

    // Function to get all persons from the database
    fun getAllPersons(): LiveData<List<Person?>> {
        return personDao.getAllPersons()
    }

    // Function to get a specific person from the database by id
    fun getPerson(): LiveData<Person?> {
        return personDao.getPersonById()
    }
}


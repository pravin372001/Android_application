package com.example.todo.addtodo

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todo.database.ToDoDatabaseDao

class AddTodoViewModelFactory(
    private val database: ToDoDatabaseDao,
    private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AddTodoViewModel::class.java)) {
            return AddTodoViewModel(database, application) as T
        }
        throw IllegalArgumentException("Unkown ViewModel Class")
    }
}
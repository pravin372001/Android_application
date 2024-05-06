package com.example.todo.addtodo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.todo.database.ToDoDatabaseDao

class AddTodoViewModel(val database : ToDoDatabaseDao, application : Application) : AndroidViewModel(application){
}
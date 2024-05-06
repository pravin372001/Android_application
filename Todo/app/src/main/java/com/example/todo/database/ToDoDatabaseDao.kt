package com.example.todo.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ToDoDatabaseDao {

    @Insert
    fun insert(todo : ToDo)

    @Update
    fun update(todo : ToDo)

    @Query("Select * from todo_table where todoId = :key")
    fun getTodo(key : Long) : ToDo

    @Query("Delete from todo_table")
    fun clear()

    @Query("Select * from todo_table")
    fun getAllTodo() : LiveData<List<ToDo>>

}

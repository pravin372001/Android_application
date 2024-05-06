package com.example.todo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_table")
data class ToDo(
    @PrimaryKey(autoGenerate = true)
    var todoId : Long = 0L,

    @ColumnInfo(name = "todo_title")
    var todoTitle :String = "",

    @ColumnInfo(name = "todo_description")
    var todoDescription : String = "",

    @ColumnInfo(name = "completed")
    var completedOrNot : Boolean = false
)
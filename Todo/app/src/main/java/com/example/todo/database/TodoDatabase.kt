package com.example.todo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ToDo::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {
    abstract val todoDatabaseDao : ToDoDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE : TodoDatabase? = null

        fun getInstance(context : Context) : TodoDatabase {
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext
                                                    , TodoDatabase::class.java
                                                    , "TodoDatabase")
                        .fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
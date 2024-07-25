package com.example.znews.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NewsModel::class, NewsOneModel::class], version = 1, exportSchema = false)
abstract class NewsDatabase: RoomDatabase() {
    abstract fun newsDao(): NewsDao
    abstract fun newsOneDao(): NewOneDao

    companion object {
        @Volatile
        private var INSTANCE: NewsDatabase? = null

        fun getDatabase(context: Context): NewsDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    "news_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
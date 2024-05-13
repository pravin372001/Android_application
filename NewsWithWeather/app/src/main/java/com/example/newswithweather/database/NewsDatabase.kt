package com.example.newswithweather.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.newswithweather.model.news.News

@Database(entities = [NewsModel::class, CurrentWeather::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao
    abstract fun weatherDao(): WeatherDao

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
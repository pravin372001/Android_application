package com.example.roomretrofitrecycler.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.roomretrofitrecycler.model.Gender

@Database(entities = [Gender::class], version = 1, exportSchema = false)
abstract class GenderDatabase : RoomDatabase() {

    abstract fun genderDao(): GenderDao

    companion object {
        @Volatile
        private var INSTANCE: GenderDatabase? = null

        fun getDatabase(context: Context): GenderDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GenderDatabase::class.java,
                    "gender_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

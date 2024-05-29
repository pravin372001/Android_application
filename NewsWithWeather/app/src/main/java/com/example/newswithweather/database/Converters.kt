package com.example.newswithweather.database

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Converters {
    private val formatter = SimpleDateFormat("EEEE, dd MMM, yyyy hh:mm a", Locale.ENGLISH)

    @TypeConverter
    fun fromTimestamp(value: String?): Date? {
        return value?.let {
            formatter.parse(it)
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): String? {
        return date?.let {
            formatter.format(it)
        }
    }
}

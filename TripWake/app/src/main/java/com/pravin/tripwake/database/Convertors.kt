package com.pravin.tripwake.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.android.gms.maps.model.LatLng

class Converters {

    @TypeConverter
    fun fromLatLng(latLng: LatLng): String {
        return Gson().toJson(latLng)
    }

    @TypeConverter
    fun toLatLng(latLngString: String): LatLng {
        return Gson().fromJson(latLngString, LatLng::class.java)
    }

    @TypeConverter
    fun fromLatLngList(latLngList: List<LatLng>): String {
        return Gson().toJson(latLngList)
    }

    @TypeConverter
    fun toLatLngList(latLngListString: String): List<LatLng> {
        val listType = object : TypeToken<List<LatLng>>() {}.type
        return Gson().fromJson(latLngListString, listType)
    }
}

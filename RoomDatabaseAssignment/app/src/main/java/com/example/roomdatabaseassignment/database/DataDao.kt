package com.example.roomdatabaseassignment.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DataDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertData(dataEntity: Data)

    @Query("SELECT * FROM data_table")
    fun getAllData(): LiveData<List<Data>>

    @Query("DELETE FROM data_table")
    fun deleteData()
}

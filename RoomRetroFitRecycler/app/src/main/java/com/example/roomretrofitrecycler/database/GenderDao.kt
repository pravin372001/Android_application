package com.example.roomretrofitrecycler.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.roomretrofitrecycler.model.Gender

@Dao
interface GenderDao {

    @Insert
    suspend fun insertGender(gender: Gender)

    @Query("SELECT * FROM gender_table")
    fun getAllGenders(): LiveData<List<Gender>>

    @Query("SELECT * FROM gender_table WHERE id = :id")
    suspend fun getGenderById(id: Int): Gender?

    @Update
    suspend fun updateGender(gender: Gender)

    @Query("DELETE FROM gender_table WHERE id = :id")
    suspend fun deleteGenderById(id: Int)

    @Query("DELETE FROM gender_table")
    suspend fun deleteAllGenders()

    @Query("UPDATE sqlite_sequence SET SEQ=0 WHERE NAME='gender_table'")
    suspend fun resetTable()
}

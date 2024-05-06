package com.example.roomdatabse.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PersonDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(person: Person)

    @Query("Delete From person_table")
    suspend fun delete()

    @Query("SELECT * FROM person_table")
    fun getAllPersons(): LiveData<List<Person?>>

    @Query("SELECT * FROM person_table ORDER BY id DESC LIMIT 1")
    fun getPersonById(): LiveData<Person?>
}

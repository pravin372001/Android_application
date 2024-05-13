package com.example.roomretrofitrecycler.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.roomretrofitrecycler.database.GenderDao
import com.example.roomretrofitrecycler.database.GenderDatabase
import com.example.roomretrofitrecycler.model.Gender

class GenderRepository(context: Context) {
    private val genderDao: GenderDao
    init {
        val database = GenderDatabase.getDatabase(context)
        genderDao = database.genderDao()
    }
    suspend fun insertGender(gender: Gender) {
        genderDao.insertGender(gender)
    }

    suspend fun updateGender(gender: Gender) {
        genderDao.updateGender(gender)
    }

    suspend fun deleteGenderById(id: Int) {
        genderDao.deleteGenderById(id)
    }

    suspend fun deleteAllGenders() {
        genderDao.deleteAllGenders()
    }

    suspend fun resetSequence(){
        genderDao.resetTable()
    }

    suspend fun getGenderById(id: Int): Gender? {
        return genderDao.getGenderById(id)
    }

    fun getAllGendersList(): LiveData<List<Gender>>{
        return genderDao.getAllGenders()
    }
}

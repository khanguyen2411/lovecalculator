package com.hola360.crushlovecalculator.data.repository

import android.app.Application
import com.hola360.crushlovecalculator.data.model.diary.DiaryModel
import com.hola360.crushlovecalculator.data.room.AppDatabase
import com.hola360.crushlovecalculator.data.room.DiaryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DiaryRepository(app: Application) {

    private val diaryDAO: DiaryDao = AppDatabase.getInstance(app).diaryDao()

    suspend fun addDiary(diaryModel: DiaryModel) = withContext(Dispatchers.Default) {
        try {
            diaryDAO.addDiary(diaryModel)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    suspend fun updateDiary(diaryModel: DiaryModel): Boolean = withContext(Dispatchers.Default) {
        try {
            diaryDAO.updateDiary(diaryModel)
            true
        } catch (ex: Exception) {
            false
        }
    }

    suspend fun deleteDiary(diaryModel: DiaryModel): Boolean = withContext(Dispatchers.Default) {
        try {
            diaryDAO.deleteDiary(diaryModel)
            true
        } catch (ex: Exception) {
            false
        }
    }

    suspend fun getAllDiary(): List<DiaryModel>? = withContext(Dispatchers.Default) {
        try {
            diaryDAO.getAllDiary()
        } catch (ex: Exception) {
            null
        }
    }

    suspend fun checkDateHasDiary(date: Long): Boolean = withContext(Dispatchers.Default) {
        try {
            diaryDAO.checkDateHasDiary(date) != 0
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }

    suspend fun getDiaryByDate(date: Long): List<DiaryModel>? = withContext(Dispatchers.Default) {
        try {
            diaryDAO.getDiaryByDate(date)
        } catch (ex: Exception) {
            null
        }
    }

    suspend fun getDiaryByKeyword(keyword: String): List<DiaryModel>? =
        withContext(Dispatchers.Default) {
            try {
                diaryDAO.getDiaryByKeyword(keyword)
            } catch (ex: Exception) {
                null
            }
        }
}
package com.hola360.crushlovecalculator.data.room

import androidx.room.*
import com.hola360.crushlovecalculator.data.model.diary.DiaryModel

@Dao
interface DiaryDao {
    @Insert
    suspend fun addDiary(diaryModel: DiaryModel)

    @Update
    suspend fun updateDiary(diaryModel: DiaryModel)

    @Delete
    suspend fun deleteDiary(diaryModel: DiaryModel)

    @Query("Select * from tblDiary order by time DESC")
    suspend fun getAllDiary(): List<DiaryModel>

    @Query("select count(*) from tblDiary where time >= (:dateTime) and time <= (:dateTime + 86400000)")
    suspend fun checkDateHasDiary(dateTime: Long): Int

    @Query("select * from tblDiary where time >= (:dateTime) and time <= (:dateTime + 86400000)")
    suspend fun getDiaryByDate(dateTime: Long): List<DiaryModel>

    @Query("select * from tblDiary where (title like '%' || :keyword || '%') or (description like '%' || :keyword || '%') ")
    suspend fun getDiaryByKeyword(keyword: String): List<DiaryModel>
}
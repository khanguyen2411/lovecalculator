package com.hola360.crushlovecalculator.data.room

import androidx.room.*
import com.hola360.crushlovecalculator.data.model.ImageStore

@Dao
interface ImageStoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chatModel: ImageStore): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(chatModels: MutableList<ImageStore>)

    @Query("Delete from ImageStore where itemId = :itemId ")
    suspend fun deleteByItemId(itemId: String)

    @Query("Select * from ImageStore where 1 = 1")
    suspend fun getAll(): MutableList<ImageStore>?


    @Query("Select * from ImageStore where itemId = :itemId")
    suspend fun getByItemId(itemId: String): ImageStore?

    @Query("Delete from ImageStore where 1 = 1")
    suspend fun deleteAll()
}
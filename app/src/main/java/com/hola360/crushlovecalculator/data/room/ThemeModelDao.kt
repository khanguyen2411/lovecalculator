package com.hola360.crushlovecalculator.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hola360.crushlovecalculator.data.model.theme.ThemeModel

@Dao
interface ThemeModelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteTheme(themeModel: ThemeModel)

    @Query("select count(*) from tblFavoriteTheme where item_id = (:itemId)")
    suspend fun isExists(itemId: String) : Int
}
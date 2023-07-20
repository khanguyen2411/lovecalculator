package com.hola360.crushlovecalculator.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hola360.crushlovecalculator.data.model.ImageStore
import com.hola360.crushlovecalculator.data.model.diary.DiaryModel
import com.hola360.crushlovecalculator.data.model.event.EventModel
import com.hola360.crushlovecalculator.data.model.story.StoryModel
import com.hola360.crushlovecalculator.data.model.theme.ThemeModel

@Database(entities = [ImageStore::class, ThemeModel::class, EventModel::class, StoryModel::class, DiaryModel::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageStoreDao
    abstract fun themeDao(): ThemeModelDao
    abstract fun eventDao(): MyEventDao
    abstract fun storyDao(): StoryDao
    abstract fun diaryDao(): DiaryDao

    companion object {
        private const val DATABASE_NAME = "APP_DB"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
        }
    }
}
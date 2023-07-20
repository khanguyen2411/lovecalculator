package com.hola360.crushlovecalculator.utils

import android.content.Context
import java.io.File

class RootPath private constructor() {
    companion object {
        private const val THEME_DIR = "Themes"
        private const val TEMP_DIR = "Temp"
        private const val EVENT_DIR = "Event"
        private const val DIARY_DIR = "Diary"
        private var instance: RootPath? = null
        fun getInstance(): RootPath {
            if (instance == null) {
                instance = RootPath()
            }
            return instance!!
        }
    }

    fun getThemeFolder(context: Context): File {
        val file = File(context.cacheDir.absolutePath, THEME_DIR)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    fun getTempFolder(context: Context): File {
        val file = File(context.cacheDir.absolutePath, TEMP_DIR)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    fun getEventFolder(context: Context): File {
        val file = File(context.cacheDir.absolutePath, EVENT_DIR)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    fun getEventIdFolder(context: Context, eventId: Int): File {
        val file = File(context.cacheDir.absolutePath, "$EVENT_DIR/$eventId")
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    fun getStoryFolder(context: Context, eventId: Int, time: Long): File {
        val file = File(context.cacheDir.absolutePath, "$EVENT_DIR/$eventId/$time")
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    fun getDiaryFolder(context: Context, time: Long): File {
        val file = File(context.cacheDir.absolutePath, "$DIARY_DIR/$time")
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }
}
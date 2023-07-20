package com.hola360.crushlovecalculator.data.repository

import android.content.Context
import com.google.gson.Gson
import com.hola360.crushlovecalculator.data.model.lovetime.LoveTimeTheme
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.RootPath
import com.hola360.crushlovecalculator.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.zeroturnaround.zip.ZipUtil
import java.io.File
import java.io.IOException

class Repository {

    suspend fun getTheme(context: Context, isPreview: Boolean): LoveTimeTheme? =
        withContext(Dispatchers.Default) {
            val themeFolder = if (isPreview) {
                RootPath.getInstance().getTempFolder(context)
            } else {
                RootPath.getInstance().getThemeFolder(context)
            }
            val configurationJsonFile = File(themeFolder, Constants.JSON_THEME_FILE)
            if (!configurationJsonFile.exists()) {
                if (!unPackDefault(context, themeFolder)) {
                    null
                }
            }
            val jsoString = Utils.getStringFromFile(configurationJsonFile)
            if (jsoString != null && jsoString.isNotEmpty()) {
                val gson = Gson()
                gson.fromJson(jsoString, LoveTimeTheme::class.java)
            } else {
                null
            }
        }

    suspend fun unPack(fileZip: File, outputFolder: File): Boolean = withContext(Dispatchers.IO) {
        outputFolder.delete()
        ZipUtil.unpack(fileZip, outputFolder)
        true
    }

    suspend fun unPackDefault(context: Context, outputFolder: File): Boolean =
        withContext(Dispatchers.IO) {
            outputFolder.delete()
            val inputStream = try {
                context.assets.open(Constants.DEFAULT_THEME_PATH)
            } catch (ex: IOException) {
                null
            }
            if (inputStream != null) {
                ZipUtil.unpack(inputStream, outputFolder)
                true
            } else {
                false
            }
        }
}
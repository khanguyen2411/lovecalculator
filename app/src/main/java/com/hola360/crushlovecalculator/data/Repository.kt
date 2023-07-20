package com.hola360.crushlovecalculator.data

import android.content.Context
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.loader.MediaLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository {
    suspend fun getAllLovePhoto(
        context: Context,
        isOldestDateFirst: Boolean, isDetail: Boolean
    ): MutableList<LovePhotoModel> =
        withContext(Dispatchers.IO) {
            val allLovePhotos = MediaLoader.getAllLovePhoto(context, isOldestDateFirst, isDetail)
            if (allLovePhotos.isNotEmpty() && !isDetail) {
                val groupTitles = mutableListOf<String>()
                val finalSongList = mutableListOf<LovePhotoModel>()
                for (localSongModel in allLovePhotos) {
                    if (!groupTitles.contains(localSongModel.timeInGroup)) {
                        groupTitles.add(localSongModel.timeInGroup!!)
                        val header = LovePhotoModel(true, localSongModel.timeInGroup)
                        finalSongList.add(header)
                    }
                    finalSongList.add(localSongModel)
                }
                finalSongList
            } else {
                allLovePhotos
            }

        }

    @Throws(SecurityException::class)
    suspend fun deleteFiles(
        context: Context,
        localPhotoModels: List<LovePhotoModel>
    ): MutableList<LovePhotoModel> = withContext(Dispatchers.IO) {
        SystemUtils.deleteFiles(context, localPhotoModels)
    }
}
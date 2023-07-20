package com.hola360.crushlovecalculator.data.repository


import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.app.App
import com.hola360.crushlovecalculator.data.model.PhotoModel
import com.hola360.crushlovecalculator.utils.loader.PhotoLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaStoreRepository(val app: App) {

    suspend fun getImages(): List<PhotoModel> = withContext(Dispatchers.Default) {
        PhotoLoader.loadAllImages(app)
    }

    suspend fun getAlbums(
        curAlbumId: Long,
        photoModels: List<PhotoModel>
    ): MutableList<PhotoModel> =
        withContext(Dispatchers.Default) {
            val albums = mutableListOf<PhotoModel>()
            if (photoModels.isNotEmpty()) {
                val allPhotoItem = PhotoModel(
                    photoModels[0].uri,
                    photoModels[0].file,
                    app.getString(R.string.title_photos),
                    -1,
                    app.getString(R.string.title_photos)
                )
                allPhotoItem.childCount = photoModels.size
                allPhotoItem.isChecked = curAlbumId == allPhotoItem.albumId
                albums.add(
                    allPhotoItem
                )
                val maps = photoModels.groupBy { it.albumId }
                if (maps.isNotEmpty()) {
                    maps.values.forEach {
                        it[0].childCount = it.size
                        it[0].isChecked = curAlbumId == it[0].albumId
                        albums.add(it[0])
                    }
                }
            }
            albums.forEach {

            }
            albums
        }

    suspend fun getAlbumDetail(
        photoModels: List<PhotoModel>,
        albumId: Long
    ): MutableList<PhotoModel> =
        withContext(Dispatchers.Default) {
            val albums = mutableListOf<PhotoModel>()
            if (photoModels.isNotEmpty()) {
                val maps = photoModels.filter { it.albumId == albumId }
                if (maps.isNotEmpty()) {
                    albums.addAll(maps)
                }
            }
            albums
        }

}
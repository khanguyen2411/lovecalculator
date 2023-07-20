package com.hola360.crushlovecalculator.data.repository

import android.app.Application
import com.hola360.crushlovecalculator.data.CountType
import com.hola360.crushlovecalculator.data.api.imagestore.RetrofitHelper
import com.hola360.crushlovecalculator.data.model.ImageStore
import com.hola360.crushlovecalculator.data.model.submit.SubmitRootData
import com.hola360.crushlovecalculator.data.model.theme.ThemeDataAppsResponse
import com.hola360.crushlovecalculator.data.model.theme.ThemeModel
import com.hola360.crushlovecalculator.data.model.theme.ThemeStoreResponse
import com.hola360.crushlovecalculator.data.response.imagestore.ImageStoreReponse
import com.hola360.crushlovecalculator.data.response.imagestore.emoijconnect.EmoijConnectResponse
import com.hola360.crushlovecalculator.data.response.imagestore.storeBg.CoverStoreResponse
import com.hola360.crushlovecalculator.data.room.AppDatabase
import com.hola360.crushlovecalculator.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageStoreRepository(app: Application) {
    private val storeHelper: RetrofitHelper = RetrofitHelper.getInstance()
    private val imageStoreDao = AppDatabase.getInstance(app).imageDao()
    private val themeModelDao = AppDatabase.getInstance(app).themeDao()


    suspend fun getCoverStore(type: String): CoverStoreResponse? = withContext(
        Dispatchers.Default
    ) {
        try {
            storeHelper.storeApi.getCoverStore(type)
        } catch (ex: Exception) {
            null
        }
    }

    suspend fun getImageStore(
        sort: String,
        page: Int,
        limit: Int
    ): ImageStoreReponse? = withContext(
        Dispatchers.Default
    ) {
        try {
            storeHelper.storeApi.getImagesStore(Constants.LIST_BG_PARAM, sort, page, limit)
        } catch (ex: Exception) {
            null
        }
    }

    suspend fun getTheme(
        sort: String,
    ): ThemeStoreResponse? = withContext(
        Dispatchers.Default
    ) {
        try {
            storeHelper.storeApi.getTheme(Constants.LIST_THEME_PARAM, sort)
        } catch (ex: Exception) {
            null
        }
    }

    suspend fun getImageEmoji(type: String): EmoijConnectResponse? = withContext(
        Dispatchers.Default
    ) {
        try {
            storeHelper.storeApi.getIconEmoji(type)
        } catch (ex: Exception) {
            null
        }
    }

    suspend fun pushCount(
        itemId: String,
        countType: CountType, isTheme: Boolean
    ): SubmitRootData? = withContext(
        Dispatchers.Default
    ) {
        try {
            storeHelper.storeApi.pushCount(
                if (isTheme) {
                    Constants.PUSH_COUNT_THEME
                } else {
                    Constants.PUSH_COUNT
                }, itemId, countType.name.lowercase()
            )
        } catch (ex: Exception) {
            null
        }
    }

    suspend fun addFavoriteTheme(themeModel: ThemeModel) = withContext(Dispatchers.Default){
        try {
            themeModelDao.addFavoriteTheme(themeModel)
        } catch (ex: Exception){ }
    }

    suspend fun isExists(itemId: String) : Boolean = withContext(Dispatchers.Default){
        try {
            return@withContext themeModelDao.isExists(itemId) == 1
        } catch (ex: Exception){
            false
        }
    }
    suspend fun getFavoriteById(itemId: String): ImageStore? = withContext(Dispatchers.Default) {
        imageStoreDao.getByItemId(itemId)
    }

    suspend fun getAllFavorites(): MutableList<ImageStore>? = withContext(Dispatchers.Default) {
        imageStoreDao.getAll()
    }

    suspend fun addToFavorite(image: ImageStore) = withContext(Dispatchers.Default) {
        imageStoreDao.insert(image)
    }

    suspend fun remove(image: ImageStore) = withContext(Dispatchers.Default) {
        imageStoreDao.deleteByItemId(image.itemId!!)
    }
}
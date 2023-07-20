package com.hola360.crushlovecalculator.ui.lovetime.preview

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hola360.crushlovecalculator.data.CountType
import com.hola360.crushlovecalculator.data.model.theme.ThemeModel
import com.hola360.crushlovecalculator.data.repository.ImageStoreRepository
import com.hola360.crushlovecalculator.ui.lovetime.base.BaseLoveTimeViewModel
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.RootPath
import com.hola360.crushlovecalculator.utils.ToastUtils
import kotlinx.coroutines.launch
import java.io.File

class ThemePreviewViewModel(private val application: Application) :
    BaseLoveTimeViewModel(application) {
    val imRepository = ImageStoreRepository(application)

    fun pushCountTheme(itemId: String) {
        viewModelScope.launch {
            try {
                val isExists = imRepository.isExists(itemId)
                if (!isExists) {
                    imRepository.addFavoriteTheme(ThemeModel(itemId))
                    val response = imRepository.pushCount(itemId, CountType.Like, true)
                    if (response != null) {
                        if (response.data.submitResult.status) {
                            ToastUtils.getInstance(application.applicationContext)
                                .showToast("Liked")
                        }
                    }
                }
            } catch (ex: Exception) {
                ToastUtils.getInstance(application.applicationContext)
                    .showToast("Something got error, please check and try again")
            }
        }
    }

    fun installTheme() {
        viewModelScope.launch {
            val zipFile =
                File(
                    RootPath.getInstance().getTempFolder(application.applicationContext),
                    Constants.TEMP_ZIP_FILE
                )
            val themeFolder = RootPath.getInstance().getThemeFolder(application)
            repository.unPack(zipFile, themeFolder)
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ThemePreviewViewModel::class.java)) {
                return ThemePreviewViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}
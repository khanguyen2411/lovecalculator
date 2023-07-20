package com.hola360.crushlovecalculator.ui.lovetime.base

import android.app.Application
import androidx.lifecycle.*
import com.caverock.androidsvg.SVG
import com.hola360.crushlovecalculator.data.model.lovetime.LoveTimeModel
import com.hola360.crushlovecalculator.data.repository.Repository
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.RootPath
import com.hola360.crushlovecalculator.utils.SharedPreferenceUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream

abstract class BaseLoveTimeViewModel(private val application: Application) : ViewModel() {
    val repository = Repository()
    val loveTimeLiveData =
        MutableLiveData<DataResponse<LoveTimeModel>>(DataResponse.DataEmptyResponse())

    val isEmpty: LiveData<Boolean> = Transformations.map(loveTimeLiveData) {
        loveTimeLiveData.value is DataResponse.DataErrorResponse
    }

    val isLoading: LiveData<Boolean> = Transformations.map(loveTimeLiveData) {
        loveTimeLiveData.value is DataResponse.DataLoadingResponse
    }

    val mLoveTimeModel: LiveData<LoveTimeModel?> = Transformations.map(loveTimeLiveData) {
        if (loveTimeLiveData.value is DataResponse.DataSuccessResponse) {
            val body = (loveTimeLiveData.value as DataResponse.DataSuccessResponse).body
            body
        } else {
            null
        }
    }

    fun fetchLoveTime(isPreview: Boolean) {
        if (loveTimeLiveData.value !is DataResponse.DataLoadingResponse) {
            val loveTimeModel = LoveTimeModel()
            viewModelScope.launch {
                try {
                    val theme = repository.getTheme(application, isPreview)
                    val rootPath = if (isPreview) {
                        RootPath.getInstance().getTempFolder(application)
                    } else {
                        RootPath.getInstance().getThemeFolder(application)
                    }

                    if (theme == null) {
                        loveTimeLiveData.value = DataResponse.DataErrorResponse(null)
                    } else {
                        val bigHeartTask = async {
                            if (theme.hasBigHeart == 1) {
                                val bigHeart = FileInputStream(
                                    File(
                                        rootPath,
                                        Constants.THEME_BIG_HEART
                                    )
                                )
                                SVG.getFromInputStream(bigHeart)
                            } else {
                                null
                            }
                        }

                        val emojiTask = async {
                            val emojiFile = File(
                                rootPath,
                                Constants.THEME_EMOJI
                            )
                            val emoji = if (emojiFile.exists()) {
                                FileInputStream(emojiFile)
                            } else {
                                application.assets.open("theme/emoji.svg")
                            }

                            SVG.getFromInputStream(emoji)
                        }

                        loveTimeModel.startDate =
                            SharedPreferenceUtils.getInstance(application).getStartLovingDate()
                        loveTimeModel.bigHeartSvg = bigHeartTask.await()
                        loveTimeModel.emojiSvg = emojiTask.await()

                        if (isPreview) {
                            loveTimeModel.bgPath = File(
                                rootPath,
                                Constants.THEME_BACKGROUND
                            ).absolutePath
                        } else {
                            val customBgPath =
                                SharedPreferenceUtils.getInstance(application).getLoveBg()
                            if (customBgPath != null && customBgPath.isNotEmpty()) {
                                loveTimeModel.bgPath = customBgPath
                            } else {
                                loveTimeModel.bgPath = File(
                                    rootPath,
                                    Constants.THEME_BACKGROUND
                                ).absolutePath
                            }
                        }

                        loveTimeModel.avatarFile = File(
                            rootPath,
                            Constants.THEME_AVATAR
                        )
                        loveTimeModel.profileModel =
                            SharedPreferenceUtils.getInstance(application)
                                .getUserProfileModel(false)
                        loveTimeModel.crushProfile =
                            SharedPreferenceUtils.getInstance(application).getUserProfileModel(true)
                        loveTimeModel.theme = theme
                        loveTimeLiveData.value = DataResponse.DataSuccessResponse(loveTimeModel)
                    }
                } catch (ex: Exception) {
                }
            }
        }
    }
}
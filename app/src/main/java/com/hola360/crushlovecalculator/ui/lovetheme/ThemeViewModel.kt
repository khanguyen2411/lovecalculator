package com.hola360.crushlovecalculator.ui.lovetheme

import android.app.Application
import androidx.lifecycle.*
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.CountType
import com.hola360.crushlovecalculator.data.model.theme.ThemeModel
import com.hola360.crushlovecalculator.data.repository.ImageStoreRepository
import com.hola360.crushlovecalculator.data.repository.Repository
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.data.utils.StoreDataResponse
import com.hola360.crushlovecalculator.utils.RootPath
import kotlinx.coroutines.launch
import java.io.File
import java.lang.IllegalArgumentException

class ThemeViewModel(val app: Application) :
    ViewModel() {
    private val imageStoreRepository = ImageStoreRepository(app)
    private val repository = Repository()

    val installThemeLiveDate =
        MutableLiveData<DataResponse<Boolean>>(DataResponse.DataEmptyResponse())

    val uiData =
        MutableLiveData<StoreDataResponse<MutableList<ThemeModel>>>(StoreDataResponse.DataEmptyResponse())

    val isEmpty: LiveData<Boolean> = Transformations.map(uiData) {
        uiData.value!!.loadDataStatus == LoadDataStatus.ERROR
    }

    val isLoading: LiveData<Boolean> = Transformations.map(uiData) {
        uiData.value!!.loadDataStatus == LoadDataStatus.LOADING
    }

    fun pushViewCount(itemId : String){
        viewModelScope.launch {
            try {
                val response = imageStoreRepository.pushCount(itemId, CountType.View, true)
            } catch (ex: Exception){

            }
        }
    }

    fun retry() {
        fetchData(false)
    }

    fun fetchData(isRefresh: Boolean) {
        if (uiData.value!!.loadDataStatus != LoadDataStatus.LOADING
            && uiData.value!!.loadDataStatus != LoadDataStatus.REFRESH
        ) {
            if (!isRefresh) {
                uiData.value = StoreDataResponse.DataLoading(LoadDataStatus.LOADING)
            } else {
                uiData.value = StoreDataResponse.DataLoading(LoadDataStatus.REFRESH)
            }
            viewModelScope.launch {
                val imageStoreData = imageStoreRepository.getTheme(
                    "new",
                )
                if (imageStoreData != null) {
                    try {
                        val themeList = imageStoreData.data.themeListData.themeModelList
                        if (themeList.isNotEmpty()) {
                            themeList.map {
                                it.full = imageStoreData.data.themeListData.subUrl.plus(it.full)
                                it.thumb = imageStoreData.data.themeListData.subUrl.plus(it.thumb)
                                it.zip = imageStoreData.data.themeListData.subUrl.plus(it.zip)
                            }
                            uiData.value = StoreDataResponse.DataSuccessResponse(themeList, 0)
                        } else {
                            uiData.value = StoreDataResponse.DataErrorResponse(0)
                        }
                    } catch (ex: Exception) {
                        uiData.value = StoreDataResponse.DataErrorResponse(0)
                    }

                } else {
                    uiData.value = StoreDataResponse.DataErrorResponse(0)
                }

            }
        }
    }

    fun installPreviewTheme(file: File) {
        if (installThemeLiveDate.value !is DataResponse.DataLoadingResponse) {
            installThemeLiveDate.value = DataResponse.DataLoadingResponse()
            viewModelScope.launch {
//                val themeFolder = RootPath.getInstance().getThemeFolder(app)
                val themeFolder = RootPath.getInstance().getTempFolder(app)
                repository.unPack(file, themeFolder)
                installThemeLiveDate.value = DataResponse.DataSuccessResponse(true)
            }
        }
    }

    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
                return ThemeViewModel(application) as T
            }
            throw  IllegalArgumentException(application.resources.getString(R.string.unable_create_viewmodel))
        }
    }
}
package com.hola360.crushlovecalculator.ui.lovetime.store.detail

import android.app.Application
import androidx.lifecycle.*
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.CountType
import com.hola360.crushlovecalculator.data.model.ImageStore
import com.hola360.crushlovecalculator.data.repository.ImageStoreRepository
import com.hola360.crushlovecalculator.data.utils.DataResponse
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class DetailImageViewModel(val application: Application, private val originImageStore: ImageStore) :
    ViewModel() {
    private val repository = ImageStoreRepository(application)
    val uiData =
        MutableLiveData<ImageStore?>(null)
    val pushFavoriteLiveData =
        MutableLiveData<DataResponse<Boolean>>(DataResponse.DataEmptyResponse())
    val pushDownloadLiveData =
        MutableLiveData<DataResponse<Boolean>>(DataResponse.DataEmptyResponse())
    val pushCountLiveData =
        MutableLiveData<DataResponse<String?>>(DataResponse.DataEmptyResponse())
    private var isPushViewDone = false
    fun checkFavorite() {
        viewModelScope.launch {
            uiData.value = null
            val imageStore = repository.getFavoriteById(originImageStore.itemId!!)
            if (imageStore != null) {
                originImageStore.favorite = true
            }
            uiData.value = originImageStore
        }
    }

    fun clickOnFavorite() {
        viewModelScope.launch {
            if (originImageStore.favorite) {
                repository.remove(originImageStore)
            } else {
                repository.addToFavorite(originImageStore)
                pushFavorite()
            }
            originImageStore.favorite = !originImageStore.favorite
            uiData.value = originImageStore
        }
    }

    fun pushFavorite() {
        if (pushFavoriteLiveData.value is DataResponse.DataEmptyResponse
            || pushFavoriteLiveData.value is DataResponse.DataErrorResponse
        ) {
            pushFavoriteLiveData.value = DataResponse.DataLoadingResponse()
            viewModelScope.launch {
                val pushCountResponse =
                    repository.pushCount(originImageStore.itemId!!, CountType.Like,false)
                val submitResult = pushCountResponse?.data?.submitResult
                if (submitResult != null) {
                    if (submitResult.status) {
                        originImageStore.like = submitResult.currentCount
                        repository.addToFavorite(originImageStore)
                        pushFavoriteLiveData.value = DataResponse.DataSuccessResponse(true)
                    } else {
                        pushFavoriteLiveData.value = DataResponse.DataErrorResponse(null)
                    }
                } else {
                    pushFavoriteLiveData.value = DataResponse.DataErrorResponse(null)
                }
            }
        }
    }

    fun pushDownload() {
        if (pushDownloadLiveData.value is DataResponse.DataEmptyResponse
            || pushDownloadLiveData.value is DataResponse.DataErrorResponse
        ) {
            pushDownloadLiveData.value = DataResponse.DataLoadingResponse()
            viewModelScope.launch {
                val pushCountResponse =
                    repository.pushCount(originImageStore.itemId!!, CountType.Download,false)
                val submitResult = pushCountResponse?.data?.submitResult
                if (submitResult != null) {
                    if (submitResult.status) {
                        pushDownloadLiveData.value = DataResponse.DataSuccessResponse(true)
                    } else {
                        pushDownloadLiveData.value = DataResponse.DataErrorResponse(null)
                    }
                } else {
                    pushDownloadLiveData.value = DataResponse.DataErrorResponse(null)
                }
            }
        }
    }


    fun pushCount(countType: CountType) {
        if (pushCountLiveData.value !is DataResponse.DataLoadingResponse) {
            pushCountLiveData.value = DataResponse.DataLoadingResponse()
            viewModelScope.launch {
                if (!isPushViewDone) {
                    val pushCountResponse =
                        repository.pushCount(originImageStore.itemId!!, countType,false)
                    isPushViewDone = (pushCountResponse != null)
                }
                pushCountLiveData.value = DataResponse.DataSuccessResponse(null)
            }
        }
    }

    class Factory(private val application: Application, val imageStore: ImageStore) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailImageViewModel::class.java)) {
                return DetailImageViewModel(application, imageStore) as T
            }
            throw  IllegalArgumentException(application.resources.getString(R.string.unable_create_viewmodel))
        }
    }

}
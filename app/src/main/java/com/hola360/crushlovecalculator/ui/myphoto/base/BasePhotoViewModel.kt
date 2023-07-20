package com.hola360.crushlovecalculator.ui.myphoto.base

import android.content.Context
import androidx.lifecycle.*
import com.hola360.crushlovecalculator.data.LovePhotoModel
import com.hola360.crushlovecalculator.data.Repository
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import kotlinx.coroutines.launch

abstract class BasePhotoViewModel : ViewModel() {
    private val repository = Repository()
    val deleteFilesLiveData =
        MutableLiveData<DataResponse<MutableList<LovePhotoModel>>>(DataResponse.DataEmptyResponse())

    val lovePhotoLiveData =
        MutableLiveData<DataResponse<MutableList<LovePhotoModel>>>(DataResponse.DataEmptyResponse())
    val isLoading: LiveData<Boolean> = Transformations.map(lovePhotoLiveData) {
        it.loadDataStatus == LoadDataStatus.LOADING
    }
    val isEmpty: LiveData<Boolean> = Transformations.map(lovePhotoLiveData) {
        it.loadDataStatus == LoadDataStatus.ERROR
    }

    fun fetchAllPhotos(context: Context, isOldestDateFirst: Boolean,isDetail: Boolean) {
        if (lovePhotoLiveData.value !is DataResponse.DataLoadingResponse) {
            lovePhotoLiveData.value = DataResponse.DataLoadingResponse()
            viewModelScope.launch {
                val allPhotos = repository.getAllLovePhoto(context, isOldestDateFirst, isDetail)
                lovePhotoLiveData.value = if (allPhotos.isNotEmpty()) {
                    DataResponse.DataSuccessResponse(allPhotos)
                } else {
                    DataResponse.DataErrorResponse(null)
                }
            }
        }
    }

    fun deleteFiles(context: Context, localPhotoModels: List<LovePhotoModel>) {
        if (deleteFilesLiveData.value !is DataResponse.DataLoadingResponse) {
            deleteFilesLiveData.value = DataResponse.DataLoadingResponse()
            viewModelScope.launch {
                try {
                    val remainFiles = repository.deleteFiles(context, localPhotoModels)
                    deleteFilesLiveData.value = DataResponse.DataSuccessResponse(remainFiles)
                } catch (ex: SecurityException) {
                    deleteFilesLiveData.value = DataResponse.DataErrorResponse(ex)
                }
            }
        }
    }
}
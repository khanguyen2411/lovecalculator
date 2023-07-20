package com.hola360.crushlovecalculator.ui.lovetime.store.base

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hola360.crushlovecalculator.data.model.ImageStore
import com.hola360.crushlovecalculator.data.repository.ImageStoreRepository
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.data.utils.StoreDataResponse

abstract class BaseStoreViewModel(val application: Application) : ViewModel() {
    val pushCountLiveData =
        MutableLiveData<DataResponse<String?>>(DataResponse.DataEmptyResponse())
    protected val repository = ImageStoreRepository(application)
    val uiData =
        MutableLiveData<StoreDataResponse<MutableList<ImageStore>>>(StoreDataResponse.DataEmptyResponse())
    val isEmpty: LiveData<Boolean> = Transformations.map(uiData) {
        checkEmptyData()
    }
    val isLoading: LiveData<Boolean> = Transformations.map(uiData) {
        uiData.value!!.loadDataStatus == LoadDataStatus.LOADING
    }
    val isLoadingMore: LiveData<Boolean> = Transformations.map(uiData) {
        uiData.value!!.loadDataStatus == LoadDataStatus.LOADING_MORE
    }


    fun retry() {
        fetchData(false)
    }

    abstract fun fetchData(isMore: Boolean)
    abstract fun checkEmptyData(): Boolean
    abstract fun isShowRetry(): Boolean
    abstract fun emptyMessage(): String
    abstract fun emptyTitle(): String

}
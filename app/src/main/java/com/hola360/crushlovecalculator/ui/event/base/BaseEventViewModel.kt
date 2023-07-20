package com.hola360.crushlovecalculator.ui.event.base

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hola360.crushlovecalculator.data.model.event.EventModel
import com.hola360.crushlovecalculator.data.repository.EventRepository
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus

abstract class BaseEventViewModel(private val application: Application) :
    ViewModel() {
    protected val repository = EventRepository(application)

    val uiData =
        MutableLiveData<DataResponse<MutableList<EventModel>>>(DataResponse.DataEmptyResponse())

    val isLoading: LiveData<Boolean> = Transformations.map(uiData) {
        uiData.value!!.loadDataStatus == LoadDataStatus.LOADING
    }

    val isEmpty: LiveData<Boolean> = Transformations.map(uiData) {
        checkEmptyData()
    }

    abstract fun fetchData()
    abstract fun checkEmptyData(): Boolean
    abstract fun emptyMessage(): String
    abstract fun buttonText(): String
    abstract fun emptyTitle(): String
    abstract fun isCommonEvent(): Boolean
}
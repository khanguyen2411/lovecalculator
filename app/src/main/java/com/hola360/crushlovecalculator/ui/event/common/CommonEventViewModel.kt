package com.hola360.crushlovecalculator.ui.event.common

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.ui.event.base.BaseEventViewModel
import com.hola360.crushlovecalculator.utils.Constants
import kotlinx.coroutines.launch

class CommonEventViewModel(private val app: Application) : BaseEventViewModel(app) {

    override fun fetchData() {
        if (uiData.value!!.loadDataStatus != LoadDataStatus.LOADING) {
            uiData.value = DataResponse.DataLoadingResponse()
            viewModelScope.launch {
                try {
                    val response = repository.getCommonEvent(Constants.COMMON_EVENT_TYPE)
                    if (response != null) {
                        val eventList = response.data.dataApps.list
                        if (eventList != null && eventList.isNotEmpty()) {
                            uiData.value = DataResponse.DataSuccessResponse(eventList)
                        } else {
                            uiData.value = DataResponse.DataEmptyResponse()
                        }
                    } else {
                        uiData.value = DataResponse.DataErrorResponse(null)
                    }
                } catch (ex: Exception) {
                    uiData.value = DataResponse.DataErrorResponse(null)
                }
            }
        }
    }

    override fun checkEmptyData(): Boolean {
        return (uiData.value!!.loadDataStatus == LoadDataStatus.ERROR || uiData.value!!.loadDataStatus == LoadDataStatus.IDLE)
    }

    override fun emptyMessage(): String {
        return app.resources.getString(R.string.string_lovetime_emoij_you_offline)
    }

    override fun buttonText(): String {
        return app.resources.getString(R.string.retry)
    }

    override fun emptyTitle(): String {
        return app.resources.getString(R.string.string_lovetime_emoij_no_internet)
    }

    override fun isCommonEvent(): Boolean {
        return true
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CommonEventViewModel::class.java)) {
                return CommonEventViewModel(app) as T
            }
            throw IllegalArgumentException(app.resources.getString(R.string.unable_create_viewmodel))
        }
    }

    fun retry() {
        fetchData()
    }

}
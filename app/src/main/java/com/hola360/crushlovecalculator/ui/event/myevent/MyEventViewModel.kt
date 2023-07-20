package com.hola360.crushlovecalculator.ui.event.myevent

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.repository.StoryRepository
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.ui.event.base.BaseEventViewModel
import com.hola360.crushlovecalculator.utils.RootPath
import kotlinx.coroutines.launch

class MyEventViewModel(private val app: Application) : BaseEventViewModel(app) {

    val isDeleted = MutableLiveData<Boolean>().apply { postValue(false) }
    private val storyRepository = StoryRepository(app)

    override fun fetchData() {
        if (uiData.value!!.loadDataStatus != LoadDataStatus.LOADING) {
            uiData.value = DataResponse.DataLoadingResponse()
            viewModelScope.launch {
                try {
                    val eventList = repository.getAllMyEvent()
                    if (eventList != null && eventList.isNotEmpty()) {
                        uiData.value = DataResponse.DataSuccessResponse(eventList.toMutableList())
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
        return (uiData.value!!.loadDataStatus == LoadDataStatus.ERROR)
    }

    fun deleteEventById(eventId: Int) {
        viewModelScope.launch {
            storyRepository.deleteStoryByEventID(eventId)
            isDeleted.postValue(repository.deleteEvent(eventId))
            val path = RootPath.getInstance().getEventIdFolder(app.applicationContext, eventId)
            path.deleteRecursively()
        }
    }

    override fun emptyMessage(): String {
        return ""
    }

    override fun buttonText(): String {
        return app.resources.getString(R.string.my_event_empty_button_text)
    }

    override fun emptyTitle(): String {
        return app.resources.getString(R.string.my_event_empty_title)
    }

    override fun isCommonEvent(): Boolean {
        return false
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MyEventViewModel::class.java)) {
                return MyEventViewModel(app) as T
            }
            throw IllegalArgumentException(app.resources.getString(R.string.unable_create_viewmodel))
        }
    }
}
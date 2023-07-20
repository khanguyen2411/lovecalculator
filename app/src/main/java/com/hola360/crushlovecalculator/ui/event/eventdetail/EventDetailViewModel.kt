package com.hola360.crushlovecalculator.ui.event.eventdetail

import android.app.Application
import androidx.lifecycle.*
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.model.story.StoryModel
import com.hola360.crushlovecalculator.data.repository.EventRepository
import com.hola360.crushlovecalculator.data.repository.StoryRepository
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.utils.RootPath
import kotlinx.coroutines.launch

class EventDetailViewModel(private val app: Application, private val eventId: Int) :
    ViewModel() {
    private val repository = StoryRepository(app)
    private val repositoryEvent = EventRepository(app)

    val uiData =
        MutableLiveData<DataResponse<MutableList<StoryModel>>>(DataResponse.DataEmptyResponse())

    val isLoading: LiveData<Boolean> = Transformations.map(uiData) {
        uiData.value!!.loadDataStatus == LoadDataStatus.LOADING
    }

    val isEmpty: LiveData<Boolean> = Transformations.map(uiData) {
        uiData.value!!.loadDataStatus == LoadDataStatus.ERROR
    }

    val isUpdateDone = MutableLiveData<Boolean>().apply { postValue(false) }
    val isDeleteDone = MutableLiveData<Boolean>().apply { postValue(false) }

    fun fetchData() {
        if (uiData.value!!.loadDataStatus != LoadDataStatus.LOADING) {
            uiData.value = DataResponse.DataLoadingResponse()
            viewModelScope.launch {
                try {
                    val storyList = repository.getAllStory(eventId)
                    if (storyList != null && storyList.isNotEmpty()) {
                        uiData.value = DataResponse.DataSuccessResponse(storyList.toMutableList())
                    } else {
                        uiData.value = DataResponse.DataErrorResponse(null)
                    }
                } catch (ex: Exception) {
                    uiData.value = DataResponse.DataErrorResponse(null)
                }
            }
        }
    }

    fun updateBgEvent(id: Int, thumb: String) {
        viewModelScope.launch {
            isUpdateDone.postValue(repositoryEvent.updateBgEvent(id, thumb))
        }
    }

    fun deleteStory(storyModel: StoryModel) {
        viewModelScope.launch {
            isDeleteDone.postValue(repository.deleteStory(storyModel))
            val path = RootPath.getInstance()
                .getStoryFolder(app.applicationContext, storyModel.eventId, storyModel.date)
            path.deleteRecursively()
        }
    }

    class Factory(private val app: Application, private val eventId: Int) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EventDetailViewModel::class.java)) {
                return EventDetailViewModel(app, eventId) as T
            }

            throw IllegalArgumentException(app.resources.getString(R.string.unable_create_viewmodel))
        }
    }
}
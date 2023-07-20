package com.hola360.crushlovecalculator.ui.event.storydetail

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.model.story.StoryModel
import com.hola360.crushlovecalculator.data.repository.StoryRepository
import com.hola360.crushlovecalculator.utils.RootPath
import kotlinx.coroutines.launch

class StoryDetailViewModel(val app: Application) : ViewModel() {

    private val repository = StoryRepository(app)
    val isDeleteDone = MutableLiveData<Boolean>().apply { postValue(false) }
    val isEditDone = MutableLiveData<Boolean>().apply { postValue(false) }
    var storyModel = MutableLiveData<StoryModel>()

    fun getStory(storyID : Int) {
        viewModelScope.launch {
            storyModel.value = repository.getStoryById(storyID)!!
        }
    }

    fun deleteStory(storyModel: StoryModel) {
        viewModelScope.launch {
            val path = RootPath.getInstance()
                .getStoryFolder(app.applicationContext, storyModel.eventId, storyModel.date)
            path.deleteRecursively()
            isDeleteDone.postValue(repository.deleteStory(storyModel))
        }
    }


    class Factory(val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StoryDetailViewModel::class.java)) {
                return StoryDetailViewModel(app = application) as T
            }
            throw  IllegalArgumentException(application.resources.getString(R.string.unable_create_viewmodel))
        }
    }
}
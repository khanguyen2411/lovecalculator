package com.hola360.crushlovecalculator.ui.event.dialog

import android.app.Application
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.model.PhotoModel
import com.hola360.crushlovecalculator.data.model.story.StoryModel
import com.hola360.crushlovecalculator.data.repository.StoryRepository
import com.hola360.crushlovecalculator.utils.RootPath
import com.hola360.crushlovecalculator.utils.Utils
import kotlinx.coroutines.launch
import java.io.*


class StoryDialogViewModel(private val app: Application) : ViewModel() {

    val repository = StoryRepository(app)
    val isSaved = MutableLiveData<Boolean>().apply { postValue(false) }
    val isLoading = MutableLiveData<Boolean>().apply { postValue(false) }

    fun addStory(description: String, imageList: MutableList<PhotoModel>, eventId: Int) {
        val storyModel = StoryModel(eventId = eventId)
        val currentTime = System.currentTimeMillis()
        viewModelScope.launch {
            if (!isLoading.value!!) {
                try {
                    isLoading.postValue(true)
                    val outputPath =
                        RootPath.getInstance()
                            .getStoryFolder(app.applicationContext, eventId = eventId, currentTime)

                    imageList.forEach {
                        try {
                            val fileName = it.file.absolutePath.substringAfterLast("/")
                            copyFile(outputPath, photoModel = it, fileName)
                            it.apply {
                                file = File("$outputPath/${fileName}")
                                filePath = file.absolutePath
                                uriString = Uri.fromFile(it.file).toString()
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }

                    storyModel.date = currentTime
                    storyModel.description = description
                    storyModel.images = Utils.listToJson(imageList)
                    repository.addNewStory(storyModel)

                } catch (ex: Exception) {
                }

                isLoading.postValue(false)
                isSaved.postValue(true)
            }
        }
    }

    fun updateStory(
        storyModel: StoryModel,
        description: String,
        imageList: MutableList<PhotoModel>,
        eventId: Int
    ) {
        viewModelScope.launch {
            if (!isLoading.value!!) {
                isLoading.postValue(true)
                val path = RootPath.getInstance()
                    .getStoryFolder(app.applicationContext, eventId = eventId, storyModel.date)
                try {
                    checkUnusedImage(path, imageList)

                    imageList.forEach {
                        try {
                            if (!checkExists(path, it.title)) {
                                val fileName = it.file.absolutePath.substringAfterLast("/")
                                copyFile(path, photoModel = it, fileName)

                                it.apply {
                                    file = File("$path/${fileName}")
                                    filePath = file.absolutePath
                                    uriString = Uri.fromFile(it.file).toString()
                                }
                            }
                        } catch (ex: Exception) {
                        }
                    }

                    storyModel.apply {
                        this.description = description
                        this.images = Utils.listToJson(imageList)
                    }
                    repository.updateStory(storyModel)

                } catch (ex: Exception) {
                }

                isLoading.postValue(false)
                isSaved.postValue(true)
            }
        }
    }

    //check an image is exists in the story directory
    fun checkExists(path: File, title: String): Boolean {
        path.list()?.forEach {
            if (it == title) {
                return true
            }
        }
        return false
    }

    fun checkUnusedImage(path: File, imageList: MutableList<PhotoModel>) {
        path.list()?.forEach { it ->
            var isExists = false
            imageList.forEach { _it ->
                if (_it.filePath.substringAfterLast("/") == it) {
                    isExists = true
                }
            }
            if (!isExists) {
                File("$path/$it").delete()
            }
        }
    }

    private fun copyFile(_output: File, photoModel: PhotoModel, fileName: String) {
        val input: InputStream = FileInputStream(photoModel.file)
        val output: OutputStream = FileOutputStream("$_output/${fileName}")
        val buff = ByteArray(1024)
        var len: Int
        while (input.read(buff).also { len = it } > 0) {
            output.write(buff, 0, len)
        }
        input.close()
        output.close()
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StoryDialogViewModel::class.java)) {
                return StoryDialogViewModel(app) as T
            }

            throw IllegalArgumentException(app.resources.getString(R.string.unable_create_viewmodel))
        }
    }
}
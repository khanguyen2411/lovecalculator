package com.hola360.crushlovecalculator.dialog.photopicker

import androidx.lifecycle.*
import com.hola360.crushlovecalculator.app.App
import com.hola360.crushlovecalculator.data.model.PhotoModel
import com.hola360.crushlovecalculator.data.repository.MediaStoreRepository
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.dialog.photopicker.data.model.PickPhotoModel
import com.hola360.crushlovecalculator.dialog.photopicker.data.model.PickPhotoType
import kotlinx.coroutines.launch

class PickPhotoViewModel(val app: App) : ViewModel() {
    private val repository = MediaStoreRepository(app)
    val allImages = mutableListOf<PhotoModel>()
    var curAlbumId = -1L
    val mPickPhotoModel =
        MutableLiveData<DataResponse<PickPhotoModel>>(DataResponse.DataEmptyResponse())
    val isExitDialog = MutableLiveData(false)
    val isEmpty: LiveData<Boolean> = Transformations.map(mPickPhotoModel) {
        mPickPhotoModel.value!!.loadDataStatus == LoadDataStatus.ERROR
    }

    val isLoading: LiveData<Boolean> = Transformations.map(mPickPhotoModel) {
        mPickPhotoModel.value!!.loadDataStatus == LoadDataStatus.LOADING
    }
    val isAlbumModel: LiveData<Boolean> = Transformations.map(mPickPhotoModel) {
        if (mPickPhotoModel.value!!.loadDataStatus == LoadDataStatus.SUCCESS) {
            (mPickPhotoModel.value as DataResponse.DataSuccessResponse).body.pickPhotoType == PickPhotoType.Album
        } else {
            false
        }
    }

    fun onClose() {
        if (mPickPhotoModel.value is DataResponse.DataSuccessResponse) {
            val pickPhotoModel = (mPickPhotoModel.value as DataResponse.DataSuccessResponse).body
            if (pickPhotoModel.pickPhotoType == PickPhotoType.Album) {
                loadingAll(curAlbumId)
            } else {
                isExitDialog.value = true
            }
        } else {
            isExitDialog.value = true
        }
    }


    fun loadAlbum() {
        if (mPickPhotoModel.value !is DataResponse.DataLoadingResponse) {
            if (mPickPhotoModel.value is DataResponse.DataSuccessResponse) {
                val body = (mPickPhotoModel.value as DataResponse.DataSuccessResponse).body
                if (body.pickPhotoType == PickPhotoType.Photo) {
                    mPickPhotoModel.value = DataResponse.DataLoadingResponse()
                    viewModelScope.launch {
                        if (allImages.isNotEmpty()) {
                            val pickPhotoModel =
                                PickPhotoModel(
                                    repository.getAlbums(curAlbumId, allImages),
                                    PickPhotoType.Album
                                )
                            mPickPhotoModel.value =
                                DataResponse.DataSuccessResponse(pickPhotoModel)
                        } else {
                            mPickPhotoModel.value = DataResponse.DataErrorResponse(null)
                        }
                    }
                } else {
                    loadingAll(curAlbumId)
                }
            }

        }
    }

    fun loadingAll(albumId: Long) {
        if (mPickPhotoModel.value !is DataResponse.DataLoadingResponse) {
            curAlbumId = albumId
            mPickPhotoModel.value = DataResponse.DataLoadingResponse()
            viewModelScope.launch {
                if (allImages.isEmpty()) {
                    val images = repository.getImages()
                    if (images.isNotEmpty()) {
                        allImages.addAll(images)
                    }
                }
                if (allImages.isNotEmpty()) {
                    val pickPhotoModel = if (albumId == -1L) {
                        PickPhotoModel(allImages, PickPhotoType.Photo)
                    } else {
                        PickPhotoModel(
                            repository.getAlbumDetail(allImages, albumId),
                            PickPhotoType.Photo
                        )
                    }
                    mPickPhotoModel.value = DataResponse.DataSuccessResponse(pickPhotoModel)
                } else {
                    mPickPhotoModel.value = DataResponse.DataErrorResponse(null)
                }
            }
        }
    }


    class Factory(private val app: App) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PickPhotoViewModel::class.java)) {
                return PickPhotoViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
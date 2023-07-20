package com.hola360.crushlovecalculator.ui.lovetime.emoijconnect

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.hola360.crushlovecalculator.data.model.EmoijConnect
import com.hola360.crushlovecalculator.data.repository.ImageStoreRepository
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.utils.Constants
import kotlinx.coroutines.launch

class EmojiViewModel (app : Application) :ViewModel(){
    private val repository = ImageStoreRepository(app)
    val uiData = MutableLiveData<DataResponse<List<EmoijConnect>>>()

    init {
        uiData.value = DataResponse.DataEmptyResponse()
    }
    val isEmpty : LiveData<Boolean> = Transformations.map(uiData){
        uiData.value!!.loadDataStatus == LoadDataStatus.ERROR
    }

    val isLoading: LiveData<Boolean> = Transformations.map(uiData) {
        uiData.value!!.loadDataStatus == LoadDataStatus.LOADING
    }

    fun fetchData(loading : Boolean) {
        if (uiData.value!!.loadDataStatus != LoadDataStatus.LOADING && uiData.value!!.loadDataStatus != LoadDataStatus.REFRESH) {
            if(loading){
                uiData.value = DataResponse.DataLoadingResponse()
            }

            viewModelScope.launch {
                val emoijConnectData = repository.getImageEmoji(Constants.EMOIJ_PARAM)
                try {
                    val listEmoijConect = emoijConnectData!!.data.data_apps.list
                    if(listEmoijConect != null && listEmoijConect.isNotEmpty()){
                        listEmoijConect.map {
                            it.png = emoijConnectData.data.data_apps.sub_url.plus(it.png)
                            Log.d("agsad",""+emoijConnectData.data.data_apps.sub_url.plus(it.png))
                            it.svg = emoijConnectData.data.data_apps.sub_url.plus(it.svg)
                        }
                    }
                    uiData.value = DataResponse.DataSuccessResponse(listEmoijConect)
                } catch (ex: Exception) {
                    uiData.value = DataResponse.DataErrorResponse(null)
                }
            }
        }
    }
    fun retry(){
        fetchData(true)
    }

    class Factory(private val app: Application) : ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EmojiViewModel::class.java)) {
                return EmojiViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
package com.hola360.crushlovecalculator.ui.event.store

import android.app.Application
import androidx.lifecycle.*
import com.hola360.crushlovecalculator.data.repository.ImageStoreRepository
import com.hola360.crushlovecalculator.data.response.imagestore.storeBg.ListCoverStores
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.utils.Constants
import kotlinx.coroutines.launch

class CoverStoreDiaLogFragmentViewModel(app : Application) : ViewModel() {
    private val repository = ImageStoreRepository(app)
    val uiData = MutableLiveData<DataResponse<List<ListCoverStores>>>()

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
                val listCoverStoresData = repository.getCoverStore(Constants.BANNER)
                try {
                    val listCoverStores = listCoverStoresData!!.data.data_apps.list
                    uiData.value = DataResponse.DataSuccessResponse(listCoverStores)
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
            if (modelClass.isAssignableFrom(CoverStoreDiaLogFragmentViewModel::class.java)) {
                return CoverStoreDiaLogFragmentViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
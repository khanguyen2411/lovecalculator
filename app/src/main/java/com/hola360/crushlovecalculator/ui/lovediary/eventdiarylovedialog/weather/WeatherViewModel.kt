package com.hola360.crushlovecalculator.ui.lovediary.eventdiarylovedialog.weather

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.hola360.crushlovecalculator.data.model.weather.WeatherModel
import com.hola360.crushlovecalculator.data.repository.WeatherRepository
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.utils.Constants
import kotlinx.coroutines.launch

class WeatherViewModel() : ViewModel() {
    private var responstory =  WeatherRepository()

    val uiData = MutableLiveData<DataResponse<List<WeatherModel>>>()

    init {
        uiData.value = DataResponse.DataEmptyResponse()
    }

    val isEmpty : LiveData<Boolean> = Transformations.map(uiData){
        uiData.value!!.loadDataStatus == LoadDataStatus.ERROR
    }

    val isLoading : LiveData<Boolean> = Transformations.map(uiData){
        uiData.value!!.loadDataStatus == LoadDataStatus.LOADING
    }

    fun fetchData(){
       if(uiData.value!!.loadDataStatus != LoadDataStatus.LOADING){
           uiData.value = DataResponse.DataLoadingResponse()
           viewModelScope.launch{
               var weatherData = responstory.getAllIconWeather(Constants.LIST_WEATHER_PARAM)
               try{
                   var listWeather = weatherData!!.data.data_apps.list
//                   if(listWeather != null && listWeather.isNotEmpty()){
//                       listWeather.map {
//                           it.icon = weatherData.data.data_apps.sub_url.plus(it.icon)
//                           it.title = weatherData.data.data_apps.sub_url.plus(it.title)
//                       }
//                   }
                   Log.d("lists",""+listWeather.size)
                   uiData.value = DataResponse.DataSuccessResponse(listWeather)
               }catch (ex : Exception){
                   uiData.value = DataResponse.DataErrorResponse(null)
               }
           }
       }
    }

    class Factory() : ViewModelProvider.NewInstanceFactory(){
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(WeatherViewModel ::class.java)){
                return WeatherViewModel() as T
            }
            throw  IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
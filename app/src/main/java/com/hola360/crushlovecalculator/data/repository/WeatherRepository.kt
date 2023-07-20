package com.hola360.crushlovecalculator.data.repository

import android.app.Application
import com.hola360.crushlovecalculator.data.api.imagestore.RetrofitHelper
import com.hola360.crushlovecalculator.data.response.imagestore.wearther.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository {
    private val weatherHelper: RetrofitHelper = RetrofitHelper.getInstance()

    suspend fun getAllIconWeather(type : String) : WeatherResponse? = withContext(Dispatchers.Default){
        try{
            weatherHelper.storeApi.getWeatherIcon(type)
        }catch (ex :Exception){
            null
        }
    }
}
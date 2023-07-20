package com.hola360.crushlovecalculator.data.api.imagestore

import com.google.gson.GsonBuilder
import com.hola360.crushlovecalculator.base.baseretrofit.BaseRetrofitHelper
import com.hola360.crushlovecalculator.utils.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper private constructor() : BaseRetrofitHelper() {
    var storeApi : RetrofitApi
    init {
        GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClient!!).build()

        storeApi = retrofit.create(RetrofitApi::class.java)
    }
    companion object{
        private var instance : RetrofitHelper? = null
        fun getInstance() : RetrofitHelper{
            if(instance ==  null){
                instance = RetrofitHelper()
            }
            return  instance!!
        }
    }
}
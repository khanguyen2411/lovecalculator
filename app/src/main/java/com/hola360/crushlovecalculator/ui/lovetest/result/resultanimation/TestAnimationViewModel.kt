package com.hola360.crushlovecalculator.ui.lovetest.result.resultanimation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hola360.crushlovecalculator.data.api.ResultObject
import com.hola360.crushlovecalculator.data.api.imagestore.RetrofitApi
import com.hola360.crushlovecalculator.data.api.imagestore.RetrofitHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TestAnimationViewModel: ViewModel() {

    private val retrofitApi: RetrofitApi by lazy {
        RetrofitHelper.getInstance().storeApi
    }
    val resultStringLiveData= MutableLiveData<String>()

    fun getResultString(testResult:Int, nationCode:String){
        retrofitApi.getDiscoverData(
            testResult,
            nationCode
        )
            .enqueue(object : Callback<ResultObject?> {
                override fun onResponse(
                    call: Call<ResultObject?>,
                    response: Response<ResultObject?>
                ) {
                    resultStringLiveData.value= response.body()?.result
                }

                override fun onFailure(call: Call<ResultObject?>, t: Throwable) {

                }
            })
    }
}
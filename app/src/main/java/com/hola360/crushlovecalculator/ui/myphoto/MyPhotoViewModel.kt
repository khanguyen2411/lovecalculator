package com.hola360.crushlovecalculator.ui.myphoto

import android.app.Application
import androidx.lifecycle.*
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.LovePhotoModel
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.ui.myphoto.base.BasePhotoViewModel
import kotlinx.coroutines.launch

import java.lang.IllegalArgumentException

class MyPhotoViewModel(val application: Application) : BasePhotoViewModel() {
    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MyPhotoViewModel::class.java)) {
                return MyPhotoViewModel(application) as T
            }
            throw  IllegalArgumentException(application.resources.getString(R.string.unable_create_viewmodel))
        }
    }


}
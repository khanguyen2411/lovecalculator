package com.hola360.crushlovecalculator.ui.myphoto.detail

import android.app.Application
import androidx.lifecycle.*
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.ui.myphoto.base.BasePhotoViewModel

import java.lang.IllegalArgumentException

class DetailPhotoViewModel(val application: Application) : BasePhotoViewModel() {

    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailPhotoViewModel::class.java)) {
                return DetailPhotoViewModel(application) as T
            }
            throw  IllegalArgumentException(application.resources.getString(R.string.unable_create_viewmodel))
        }
    }


}
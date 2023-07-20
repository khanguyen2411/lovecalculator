package com.hola360.crushlovecalculator.ui.lovetime

import android.app.Application
import androidx.lifecycle.*
import com.caverock.androidsvg.SVG
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.model.lovetime.LoveTimeModel
import com.hola360.crushlovecalculator.data.repository.Repository
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.ui.lovetime.base.BaseLoveTimeViewModel
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.RootPath
import com.hola360.crushlovecalculator.utils.SharedPreferenceUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream

import java.lang.IllegalArgumentException

class LoveTimeViewModel(val application: Application) : BaseLoveTimeViewModel(application) {


    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoveTimeViewModel::class.java)) {
                return LoveTimeViewModel(application) as T
            }
            throw  IllegalArgumentException(application.resources.getString(R.string.unable_create_viewmodel))
        }
    }
}
package com.hola360.crushlovecalculator.ui.lovetime.store.newbg

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.ui.lovetime.store.base.BaseLiveStoreViewModel
import com.hola360.crushlovecalculator.ui.lovetime.store.hot.HotImageViewModel
import java.lang.IllegalArgumentException

class NewImageViewModel(application: Application) : BaseLiveStoreViewModel(application, true) {
    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewImageViewModel::class.java)) {
                return NewImageViewModel(application) as T
            }
            throw  IllegalArgumentException(application.resources.getString(R.string.unable_create_viewmodel))
        }
    }
}
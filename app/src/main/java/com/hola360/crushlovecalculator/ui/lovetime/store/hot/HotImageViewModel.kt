package com.hola360.crushlovecalculator.ui.lovetime.store.hot

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.ui.lovetime.store.base.BaseLiveStoreViewModel
import com.hola360.crushlovecalculator.ui.lovetime.store.newbg.NewImageViewModel
import java.lang.IllegalArgumentException

class HotImageViewModel(application: Application) : BaseLiveStoreViewModel(application, false) {
    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HotImageViewModel::class.java)) {
                return HotImageViewModel(application) as T
            }
            throw  IllegalArgumentException(application.resources.getString(R.string.unable_create_viewmodel))
        }
    }
}
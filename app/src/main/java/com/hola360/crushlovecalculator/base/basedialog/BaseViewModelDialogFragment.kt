package com.hola360.crushlovecalculator.base.basedialog

import android.os.Bundle
import androidx.databinding.ViewDataBinding

abstract class BaseViewModelDialogFragment<V : ViewDataBinding> : BaseFullAlertDialog<V>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    abstract  fun initViewModel()
}
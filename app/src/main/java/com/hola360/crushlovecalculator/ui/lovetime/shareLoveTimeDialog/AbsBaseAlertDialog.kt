package com.hola360.crushlovecalculator.ui.lovetime.shareLoveTimeDialog

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.hola360.crushlovecalculator.base.basedialog.BaseAlertDialog

abstract class AbsBaseAlertDialog<V : ViewDataBinding> : BaseAlertDialog<V>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }
    abstract fun initViewModel()
}
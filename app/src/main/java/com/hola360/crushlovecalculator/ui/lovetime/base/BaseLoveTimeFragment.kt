package com.hola360.crushlovecalculator.ui.lovetime.base

import androidx.databinding.ViewDataBinding
import com.hola360.crushlovecalculator.base.basefragment.BasePermissionFragment

abstract class BaseLoveTimeFragment<V : ViewDataBinding> : BasePermissionFragment<V>() {

    abstract fun setNavigationIcon()
    abstract fun setToolbarMenu()
}
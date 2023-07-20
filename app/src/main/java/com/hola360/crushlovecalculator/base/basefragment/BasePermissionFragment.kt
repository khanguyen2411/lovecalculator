package com.hola360.crushlovecalculator.base.basefragment

import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.ViewDataBinding

abstract class BasePermissionFragment<V : ViewDataBinding> : BaseViewModelFragment<V>() {
    protected val storageResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            onPermissionResult()
        }

    protected abstract fun onPermissionResult()
}
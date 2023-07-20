package com.hola360.crushlovecalculator.dialog

import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseAlertDialog
import com.hola360.crushlovecalculator.databinding.DialogLoadingBinding


class LoadingDialog : BaseAlertDialog<DialogLoadingBinding>() {
    companion object {
        fun create(): LoadingDialog {
            return LoadingDialog()
        }
    }

    override fun getLayout(): Int {
        return R.layout.dialog_loading
    }

    override fun initView() {
        mDialog!!.setCancelable(false)
        isCancelable = false
    }

}
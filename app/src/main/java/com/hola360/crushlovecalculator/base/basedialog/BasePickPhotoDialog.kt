package com.hola360.crushlovecalculator.base.basedialog

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.app.App
import com.hola360.crushlovecalculator.dialog.photopicker.PickPhotoViewModel

abstract class BasePickPhotoDialog<V : ViewDataBinding?> : BaseFullAlertDialog<V>() {

    lateinit var mViewModel: PickPhotoViewModel
    lateinit var mLayoutManager: GridLayoutManager
    lateinit var app: App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
        app = requireActivity().application as App
        initViewModel()
    }

    private fun onBackPress() {
        mViewModel.onClose()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setOnKeyListener { dialog, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                onBackPress()
            }
            true
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }

    abstract fun initViewModel()
}
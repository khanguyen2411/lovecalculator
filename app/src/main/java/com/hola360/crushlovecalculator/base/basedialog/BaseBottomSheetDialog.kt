package com.hola360.crushlovecalculator.base.basedialog

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hola360.crushlovecalculator.MainActivity
import com.hola360.crushlovecalculator.R

abstract class BaseBottomSheetDialog<V : ViewDataBinding?> : BottomSheetDialogFragment() {
    protected var binding: V? = null

    protected val mainActivity by lazy {
        requireActivity() as MainActivity
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayout(), container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        dialog!!.setOnKeyListener { dialog, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                onDismiss()
            }
            true
        }
        dialog!!.setCancelable(false)
        initView()
    }

    abstract fun onDismiss()
    abstract fun initView()
    abstract fun getLayout(): Int

    override fun onDestroyView() {
        super.onDestroyView()
        if (binding != null) {
            binding!!.unbind()
            binding = null
        }
    }

}
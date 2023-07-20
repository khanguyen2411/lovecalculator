package com.hola360.crushlovecalculator.base.basedialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hola360.crushlovecalculator.MainActivity
import com.hola360.crushlovecalculator.R


abstract class BaseAlertDialog<V : ViewDataBinding?> : DialogFragment() {
    protected var binding: V? = null
    protected lateinit var mainActivity: MainActivity
    protected var curOwnerId: Int = 0
    private var builder: MaterialAlertDialogBuilder? = null
    protected var mDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = (requireActivity() as MainActivity)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding =
            DataBindingUtil.inflate(layoutInflater, getLayout(), null, false)
        builder = MaterialAlertDialogBuilder(mainActivity,R.style.MaterialAlertDialog_Rounded)
        builder!!.setView(binding!!.root)
        mDialog = builder!!.create()
        binding!!.lifecycleOwner = this
        initView()
        return mDialog!!
    }

    fun setDialogCurOwnerId(ownerId: Int) {
        this.curOwnerId = ownerId
    }

    protected abstract fun initView()
    protected abstract fun getLayout(): Int

    override fun onDestroy() {
        super.onDestroy()
        if (binding != null) {
            binding!!.unbind()
        }
    }

}
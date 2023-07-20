package com.hola360.crushlovecalculator.ui.event.store.detailsetbackgound

import android.os.Bundle
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseViewModelDialogFragment
import com.hola360.crushlovecalculator.databinding.LayoutDetailSetBgDialogBinding
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.ToastUtils

class DetailSetBgDialog : BaseViewModelDialogFragment<LayoutDetailSetBgDialogBinding>(){
    private var full : String? = null
     var onlickListerner : OnSetBgEvent? = null
    override fun initView() {
        full = requireArguments().getString(KEY_DETAIL)
        binding!!.imageDetail.let {
            Glide.with(it)
                .load(Constants.EVENT_COVER_SUB_URL + full)
                .placeholder(R.drawable.ic_default_image)
                .error(R.drawable.ic_default_image).into(it)
        }
        binding!!.btnClose.setOnClickListener(){
            dismiss()
        }
        binding!!.btnSet.setOnClickListener(){
            onlickListerner!!.onSetBgEvent(Constants.EVENT_COVER_SUB_URL+full!!)
            ToastUtils.getInstance(requireActivity())
                .showToast(getString(R.string.string_event_change_bg_success))
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        dialog!!.window!!.setDimAmount(0.8F)
    }
    override fun getLayout(): Int {
        return R.layout.layout_detail_set_bg_dialog
    }

    override fun initViewModel() {

    }

    interface OnSetBgEvent{
        fun onSetBgEvent(full :String)
    }
    companion object{
        const val KEY_DETAIL = "Key_detail"
        fun create(full :String) : DetailSetBgDialog{
        val dialog = DetailSetBgDialog()
            val bundle = Bundle()
            bundle.putString(KEY_DETAIL,full)
            dialog.arguments = bundle
            return dialog
        }
    }
}
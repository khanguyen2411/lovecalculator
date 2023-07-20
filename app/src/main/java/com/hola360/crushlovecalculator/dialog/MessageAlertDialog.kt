package com.hola360.crushlovecalculator.dialog

import android.os.Bundle
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseBottomSheetDialog
import com.hola360.crushlovecalculator.databinding.LayoutConfirmDialogBinding

class MessageAlertDialog : BaseBottomSheetDialog<LayoutConfirmDialogBinding>() {
    var onAlertMessageDialogClickListener: OnAlertMessageDialogClickListener? = null
    private var title: String? = null
    private var message: String? = null
    private var negativeText: String? = null
    private var positiveText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = requireArguments().getString(KEY_TITLE)
        message = requireArguments().getString(KEY_MESSAGE)
        negativeText = requireArguments().getString(KEY_NEGATIVE)
        positiveText = requireArguments().getString(KEY_POSITIVE)
    }

    override fun initView() {
        binding!!.message.text = message
        binding!!.title.text = title

        binding!!.apply {
            btCancel.apply {
                text = negativeText
                setOnClickListener{
                    dismiss()
                    onAlertMessageDialogClickListener?.onNegative()
                }
            }

            btAgree.apply {
                text = positiveText
                setOnClickListener {
                    dismiss()
                    onAlertMessageDialogClickListener?.onPositive()
                }
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.layout_confirm_dialog
    }

    interface OnAlertMessageDialogClickListener {
        fun onPositive()
        fun onNegative()
    }

    companion object {
        private const val KEY_TITLE = "Key_Title"
        private const val KEY_MESSAGE = "Key_Message"
        private const val KEY_POSITIVE = "Key_Positive"
        private const val KEY_NEGATIVE = "Key_Negative"
        fun create(
            title: String,
            message: String,
            negativeText: String = "Cancel",
            positiveText: String = "OK"
        ): MessageAlertDialog {
            val confirmDialog = MessageAlertDialog()
            val bundle = Bundle()
            bundle.putString(KEY_TITLE, title)
            bundle.putString(KEY_MESSAGE, message)
            bundle.putString(KEY_POSITIVE, positiveText)
            bundle.putString(KEY_NEGATIVE, negativeText)
            confirmDialog.arguments = bundle
            return confirmDialog
        }
    }

    override fun onDismiss() {
        dismiss()
    }
}
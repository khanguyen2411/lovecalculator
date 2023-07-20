package com.hola360.crushlovecalculator.dialog.action

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseBottomSheetDialog
import com.hola360.crushlovecalculator.databinding.DialogBottomsheetActionBinding
import com.hola360.crushlovecalculator.dialog.action.model.ActionAdapter
import com.hola360.crushlovecalculator.dialog.action.model.ActionModel
import kotlin.collections.ArrayList

class PickActionDiaLog : BaseBottomSheetDialog<DialogBottomsheetActionBinding>(),
    ActionAdapter.OnActionClickListener {
    var onActionPickerListener: OnActionPickerListener? = null
    var title: String? = null
    var actions: ArrayList<ActionModel>? = null
    val actionAdapter = ActionAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = requireArguments().getString(KEY_TITLE)
        actions = requireArguments().getParcelableArrayList(KEY_DATA)
    }

    override fun initView() {
        binding!!.tvTitle.text = title
        binding!!.btClose.setOnClickListener {
            onActionPickerListener?.onDismiss()
            dismiss()
        }
        binding!!.recyclerView.apply {
            adapter = actionAdapter
            layoutManager = LinearLayoutManager(requireActivity())
            setHasFixedSize(true)
        }
        actionAdapter.updateData(actions)
    }

    interface OnActionPickerListener {
        fun onItemSelected(itemPos: Int)
        fun onDismiss()
    }

    override fun getLayout(): Int {
        return R.layout.dialog_bottomsheet_action
    }

    companion object {
        private const val KEY_TITLE = "Key_title"
        private const val KEY_DATA = "Key_DATA"
        fun create(
            title: String,
            actions: ArrayList<ActionModel>,
        ): PickActionDiaLog {
            val dialog = PickActionDiaLog()
            val bundle = Bundle()
            bundle.putString(KEY_TITLE, title)
            bundle.putParcelableArrayList(KEY_DATA, actions)
            dialog.arguments = bundle
            return dialog
        }
    }

    override fun onDismiss() {
        dismiss()
        onActionPickerListener?.onDismiss()
    }

    override fun onItemActionClick(position: Int) {
        onActionPickerListener?.onItemSelected(position)
        dismiss()
    }
}
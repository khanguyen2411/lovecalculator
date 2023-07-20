package com.hola360.crushlovecalculator.ui.event.popup

import android.content.Context
import android.view.View
import androidx.appcompat.widget.ListPopupWindow
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.ui.event.adapter.ActionAdapter

class ListActionPopup(private val context: Context) {
    private val popupMenu: ListPopupWindow = ListPopupWindow(context)
    private var isItemSelected = false

    fun setup(
        anchor: View,
        actions: MutableList<EventActionModel>,
        onActionListener: ActionAdapter.OnActionListener
    ) {
        isItemSelected = false
        val adapter = ActionAdapter(actions, object : ActionAdapter.OnActionListener {
            override fun onItemClickListener(position: Int) {
                popupMenu.dismiss()
                if (!isItemSelected) {
                    isItemSelected = true
                    onActionListener.onItemClickListener(position)
                }
            }
        })

        popupMenu.apply {
            setAdapter(adapter)
            width = context.resources.getDimension(R.dimen.dp_200).toInt()
            anchorView = anchor
            isModal = true
            show()
        }
    }

    fun isShowing() : Boolean{
        return popupMenu.isShowing
    }

    fun dismiss(){
        popupMenu.dismiss()
    }
}
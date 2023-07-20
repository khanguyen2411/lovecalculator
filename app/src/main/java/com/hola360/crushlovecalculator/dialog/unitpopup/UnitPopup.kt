package com.hola360.crushlovecalculator.dialog.unitpopup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.R

class UnitPopup(val context: Context) {

    private var popupWindow: PopupWindow? = null
    private val itemList= mutableListOf<String>()
    private val adapter: ItemUnitAdapter by lazy {
        ItemUnitAdapter()
    }

    init {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_unit_popup, null)
        val recyclerView = view.findViewById<RecyclerView>(R.id.itemRv)
        recyclerView.layoutManager= LinearLayoutManager(context)
        recyclerView.adapter = adapter

        popupWindow = PopupWindow(view,
            context.resources.getDimensionPixelSize(R.dimen.dialog_unit_width),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow!!.isOutsideTouchable = true
    }

    fun updateData(itemList:MutableList<String>, callback: ItemUnitAdapter.OnUnitItemClick){
        this.itemList.clear()
        this.itemList.addAll(itemList)
        adapter.updateData(itemList, callback)
    }

    fun show(bottom: Int, anchor: View) {
        val popupItemHeight: Int =
            context.resources.getDimensionPixelSize(R.dimen.dialog_unit_item_height) * itemList.size
            + 2 * context.resources.getDimensionPixelSize(R.dimen.v_margin)
        var Yoff = 0
        val Xoff= - context.resources.getDimensionPixelSize(R.dimen.dialog_unit_width) + anchor.width
        val originalPos = IntArray(2)
        //get top, left coordinate of view in activity window
        anchor.getLocationInWindow(originalPos)
        if (bottom - originalPos[1] - anchor.height < popupItemHeight) {
            Yoff = -anchor.height - popupItemHeight
        }
        popupWindow!!.showAsDropDown(anchor, Xoff , Yoff)
    }

    fun close() {
        popupWindow?.dismiss()
    }

}
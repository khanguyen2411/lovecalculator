package com.hola360.crushlovecalculator.base.baseviewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    abstract fun onBind(position:Int)
}
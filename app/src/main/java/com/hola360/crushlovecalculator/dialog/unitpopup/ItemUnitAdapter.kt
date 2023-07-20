package com.hola360.crushlovecalculator.dialog.unitpopup

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.databinding.ItemProfileUnitBinding

class ItemUnitAdapter(): RecyclerView.Adapter<BaseViewHolder>() {
    private val itemList= mutableListOf<String>()
    private var callback:OnUnitItemClick?=null

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newItems:MutableList<String>, callback:OnUnitItemClick){
        itemList.clear()
        itemList.addAll(newItems)
        this.callback= callback
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding= ItemProfileUnitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProfileUnitViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ProfileUnitViewHolder(val binding: ItemProfileUnitBinding):BaseViewHolder(binding.root){
        override fun onBind(position: Int) {
            binding.txtUnit.text= itemList[position]
            binding.txtUnit.setOnClickListener {
                callback!!.onUnitClick(position)
            }
        }
    }

    interface OnUnitItemClick{
        fun onUnitClick(position: Int)
    }
}
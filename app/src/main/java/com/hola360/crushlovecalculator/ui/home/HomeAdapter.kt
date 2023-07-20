package com.hola360.crushlovecalculator.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.databinding.ItemHomeButtonBinding

class HomeAdapter(val callback :OnItemHomeClick ) : RecyclerView.Adapter<BaseViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding = ItemHomeButtonBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ItemHomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return 9
    }

    inner  class ItemHomeViewHolder(val binding : ItemHomeButtonBinding) : BaseViewHolder(binding.root){
        override fun onBind(position: Int) {
            val resourse = "ic_home_".plus(position)
            val resourseId = binding.root.context.resources.getIdentifier(resourse,"drawable",binding.root.context.packageName)
            binding.imgItemHome.setImageResource(resourseId)


            val txtItem = binding.root.context.resources.getStringArray(R.array.arrays_name_of_button)
            binding.txtItemHome.text = txtItem[position]

            binding.myLayoutRoot.setOnClickListener(){
                callback.onClick(position)
            }
        }
    }
    interface OnItemHomeClick{
        fun onClick(position: Int)
    }
}
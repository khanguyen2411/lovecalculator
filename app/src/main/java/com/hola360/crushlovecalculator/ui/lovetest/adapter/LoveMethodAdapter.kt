package com.hola360.crushlovecalculator.ui.lovetest.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.databinding.ItemLovetestBinding

class LoveMethodAdapter(val callback: OnItemLoveTestClick):RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding= ItemLovetestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemLoveTestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return NUMBER_TEST_METHOD
    }

    inner class ItemLoveTestViewHolder(val binding:ItemLovetestBinding): BaseViewHolder(binding.root){
        override fun onBind(position: Int) {
            val resName= "ic_lovetest_".plus(position)
            val resId= binding.root.context.resources.getIdentifier(resName, "drawable", binding.root.context.packageName)
            binding.testImage.setImageResource(resId)
            val loveTestNames= binding.root.context.resources.getStringArray(R.array.test_name)
            binding.testName.text = loveTestNames[position]
            binding.itemLayout.setOnClickListener {
                callback.onClick(position)
            }
        }
    }

    interface OnItemLoveTestClick{
        fun onClick(position: Int)
    }

    companion object{
        private const val NUMBER_TEST_METHOD=7
    }

}
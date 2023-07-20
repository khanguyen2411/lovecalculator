package com.hola360.crushlovecalculator.ui.event.storydetail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.data.model.PhotoModel
import com.hola360.crushlovecalculator.databinding.ItemStoryDetailBinding

class ViewPagerAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    val data = mutableListOf<PhotoModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(list : MutableList<PhotoModel>){
        if(list.isNotEmpty()){
            data.clear()
            data.addAll(list)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemBinding =
            ItemStoryDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class PhotoViewHolder(val binding: ItemStoryDetailBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            binding.apply {
                photoModel = data[position]
            }
        }
    }
}
package com.hola360.crushlovecalculator.ui.myphoto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.data.LovePhotoModel
import com.hola360.crushlovecalculator.databinding.ItemDetailPhotoBinding

class DetailPhotoAdapter: RecyclerView.Adapter<BaseViewHolder>() {

    val photos= mutableListOf<LovePhotoModel>()

    fun updateData(newData:MutableList<LovePhotoModel>){
        photos.clear()
        photos.addAll(newData)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding= ItemDetailPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailPhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    inner class DetailPhotoViewHolder(val binding: ItemDetailPhotoBinding):BaseViewHolder(binding.root){
        override fun onBind(position: Int) {
            Glide.with(binding.root.context).load(photos[position].uri)
                .placeholder(R.drawable.ic_default_image).error(R.drawable.ic_default_image)
                .into(binding.photo)
        }
    }
}
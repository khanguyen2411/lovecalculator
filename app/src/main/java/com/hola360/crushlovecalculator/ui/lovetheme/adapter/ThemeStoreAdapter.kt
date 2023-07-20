package com.hola360.crushlovecalculator.ui.lovetheme.adapter

import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hola360.crushlovecalculator.base.adapter.BaseStoreAdapter
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.data.model.theme.ThemeModel
import com.hola360.crushlovecalculator.databinding.ItemThemeStoreBinding
import com.hola360.crushlovecalculator.utils.clickWithDebounce

class ThemeStoreAdapter :
    BaseStoreAdapter() {
    var listener: ListenerClickItem? = null
    val data = mutableListOf<ThemeModel>()
    var mLastClick: Long = 0

    fun update(newData: List<ThemeModel>?) {
        data.clear()
        if (newData != null && newData.isNotEmpty()) {
            data.addAll(newData)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.size
    inner class PhotoViewHolder(val binding: ItemThemeStoreBinding) : BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            binding.imageStore = data[position]
            binding.myLayoutRoot.clickWithDebounce {
                if (position < itemCount) {
                    listener?.onClickFileItem(
                        position,
                        data[position]
                    )
                }
            }
        }
    }

    override fun isHeader(position: Int): Boolean {
        return data[position].header
    }

    override fun createNormalViewHolder(parent: ViewGroup): BaseViewHolder {
        val itemBinding = ItemThemeStoreBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(itemBinding)
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position].header) {
            COVER_TYPE
        } else {
            NORMAL_TYPE
        }
    }

    interface ListenerClickItem {
        fun onClickFileItem(
            position: Int,
            imageStore: ThemeModel
        )
    }

}

package com.hola360.crushlovecalculator.ui.lovetime.store.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.data.model.ImageStore
import com.hola360.crushlovecalculator.databinding.ItemImageStoreBinding
import com.hola360.crushlovecalculator.base.adapter.BaseStoreAdapter

class ImageStoreAdapter :
    BaseStoreAdapter() {
    var listener: ListenerClickItem? = null
    val data = mutableListOf<ImageStore>()
    fun update(isAddMore: Boolean, newData: List<ImageStore>?) {
        if (isAddMore) {
            val oldSize = data.size
            if (newData != null && newData.isNotEmpty()) {
                data.addAll(newData)
                notifyItemInserted(oldSize)
            }
        } else {
            data.clear()
            if (newData != null && newData.isNotEmpty()) {
                data.addAll(newData)
            }
            notifyDataSetChanged()
        }

    }


    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class PhotoViewHolder(val binding: ItemImageStoreBinding) : BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            binding.imageStore = data[position]
            binding.myLayoutRoot.setOnClickListener {
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
        val itemBinding = ItemImageStoreBinding.inflate(
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
            imageStore: ImageStore
        )
    }

}

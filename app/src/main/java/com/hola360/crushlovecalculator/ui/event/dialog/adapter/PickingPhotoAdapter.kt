package com.hola360.crushlovecalculator.ui.event.dialog.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.data.model.PhotoModel
import com.hola360.crushlovecalculator.databinding.ItemStoryImagePickingBinding
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.clickWithDebounce

class PickingPhotoAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    var onPickPhotoItemClickListener: OnPickPhotoItemClickListener? = null

    val data = mutableListOf(
        PhotoModel(isNormalItem = false)
    )

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<PhotoModel>) {
        if (newData.isNotEmpty()) {
            data.clear()
            data.add(PhotoModel(isNormalItem = false))

            val oldSize = itemCount
            if (newData.size + oldSize > 5) {
                data.removeAt(oldSize - 1)
                data.addAll(newData)
            } else {
                if (oldSize == 0) {
                    data.addAll(0, newData)
                } else {
                    data.addAll(oldSize - 1, newData)
                }
            }
            notifyDataSetChanged()
        }
    }

    fun setClickListener(listener: OnPickPhotoItemClickListener) {
        onPickPhotoItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemBinding =
            ItemStoryImagePickingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PickingPhotoViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class PickingPhotoViewHolder(private val binding: ItemStoryImagePickingBinding) :
        BaseViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        override fun onBind(position: Int) {
            binding.apply {
                photoModel = data[position]
                root.clickWithDebounce {
                    if (!data[position].isNormalItem) {
                        onPickPhotoItemClickListener!!.onAddItemClick()
                    }
                }
                ivDelete.clickWithDebounce {
                    val oldSize = itemCount
                    if (data[position].isNormalItem) {
                        onPickPhotoItemClickListener?.onDeleteItemClick(data[position])
                    }
                    data.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                    if (oldSize == Constants.STORY_IMAGE_MAXIMUM && data[3].isNormalItem) {
                        data.add(PhotoModel(isNormalItem = false))
                        notifyItemInserted(itemCount - 1)
                    }
                }
            }
        }
    }

    interface OnPickPhotoItemClickListener {
        fun onAddItemClick()
        fun onDeleteItemClick(photoModel: PhotoModel)
    }
}
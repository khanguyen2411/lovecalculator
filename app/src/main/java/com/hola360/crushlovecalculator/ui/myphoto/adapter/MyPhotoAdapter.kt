package com.hola360.crushlovecalculator.ui.myphoto.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.data.LovePhotoModel
import com.hola360.crushlovecalculator.databinding.ItemMyPhotoBinding
import com.hola360.crushlovecalculator.databinding.ItemMyPhotoTitleBinding

class MyPhotoAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    var callback: ListenerClickItem? = null
    val photos = mutableListOf<LovePhotoModel>()
    fun updateData(newData: MutableList<LovePhotoModel>?) {
        photos.clear()
        if (newData != null) {
            photos.addAll(newData)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == R.layout.item_my_photo) {
            val binding =
                ItemMyPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MyPhotoItemViewHolder(binding)
        } else {
            val binding =
                ItemMyPhotoTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MyPhotoTitleViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (!photos[position].header) {
            R.layout.item_my_photo
        } else {
            R.layout.item_my_photo_title
        }
    }

    fun getRealPos(position: Int): Int {
        return position - photos.subList(0, position).count { it.header }
    }

    inner class MyPhotoItemViewHolder(val binding: ItemMyPhotoBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            binding.myImageViewCheckBox.visibility = if (photos[position].selectMode) {
                View.VISIBLE
            } else {
                View.GONE
            }
            if (photos[position].isSelect) {
                binding.mask.visibility = View.VISIBLE
                binding.myImageViewCheckBox.setImageResource(R.drawable.ic_my_photo_select)
            } else {
                binding.mask.visibility = View.GONE
                binding.myImageViewCheckBox.setImageResource(R.drawable.ic_my_photo_radio_button_unchecked)
            }
            Glide.with(binding.root.context).load(photos[position].file)
                .placeholder(R.drawable.ic_default_image).error(R.drawable.ic_default_image)
                .override(256, 256)
                .into(binding.myImageViewLovePhoto)

            binding.myLayoutRoot.setOnClickListener {
                if (photos[position].selectMode) {
                    selectOne(position)
                } else {
                    callback?.onClickFileItem(
                        position,
                        photos[position]
                    )
                }
            }
            binding.myLayoutRoot.setOnLongClickListener {
                startSelect(position)
                true
            }
        }
    }

    fun isSelectMode(): Boolean {
        return photos.any { it.selectMode }
    }

    fun selectAll(isSelectAll: Boolean) {
        photos.forEach {
            it.selectMode = true
            it.isSelect = isSelectAll
        }
        notifyItemRangeChanged(0, photos.size)
        callback?.onUpdateCount()
    }

    private fun selectAllGroup(position: Int) {
        val isSelected = photos[position].isSelect
        val countItemInGroup = photos.count { it.timeInGroup == photos[position].timeInGroup }
        photos.filter { it.timeInGroup == photos[position].timeInGroup }
            .forEach { it.isSelect = !isSelected }
        notifyItemRangeChanged(position, countItemInGroup)
        callback?.onUpdateCount()
    }

    fun startSelect(position: Int) {
        if (!photos[position].selectMode) {
            photos.forEach { it.selectMode = true }
            notifyItemRangeChanged(0, photos.size)
        }
        selectOne(position)
    }

    fun cancelSelectMode() {
        photos.forEach {
            it.selectMode = false
            it.isSelect = false
        }
        notifyItemRangeChanged(0, photos.size)
    }

    fun countSelectedItem(): Int {
        return photos.filter { !it.header }.count { it.selectMode && it.isSelect }
    }

    fun getAllSelectedFiles(): MutableList<LovePhotoModel> {
        return photos.filter { !it.header }.filter { it.selectMode && it.isSelect }.toMutableList()
    }

    fun isSelectedAll(): Boolean {
        return photos.all { it.selectMode && it.isSelect }
    }

    private fun selectOne(position: Int) {
        photos[position].isSelect = !photos[position].isSelect
        val isAllInGroupSelected =
            photos.filter {
                !it.header && it.timeInGroup
                    .equals(photos[position].timeInGroup)
            }.all { it.isSelect }
        val groupPos =
            photos.indexOfFirst { it.header && it.timeInGroup == photos[position].timeInGroup }
        if (groupPos >= 0) {
            photos[groupPos].isSelect = isAllInGroupSelected
            notifyItemChanged(groupPos)
        }
        notifyItemChanged(position)
        callback?.onUpdateCount()
    }

    inner class MyPhotoTitleViewHolder(val binding: ItemMyPhotoTitleBinding) :
        BaseViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        override fun onBind(position: Int) {
            binding.myPhotoTitle.text = photos[position].timeInGroup
            binding.selectBox.visibility = if (photos[position].selectMode) {
                View.VISIBLE
            } else {
                View.GONE
            }
            if (photos[position].isSelect) {
                binding.selectBox.setImageResource(R.drawable.ic_my_photo_select)
            } else {
                binding.selectBox.setImageResource(R.drawable.ic_my_photo_radio_button_unchecked)
            }
            binding.myLayoutRoot.setOnClickListener {
                if (photos[position].selectMode) {
                    selectAllGroup(position)
                }
            }
            binding.myLayoutRoot.setOnLongClickListener {
                if (!photos[position].selectMode) {
                    photos.forEach { it.selectMode = true }
                    notifyItemRangeChanged(0, photos.size)
                }
                selectAllGroup(position)
                true
            }
        }
    }

    fun isHeader(position: Int): Boolean {
        return photos[position].header
    }

    interface ListenerClickItem {
        fun onClickFileItem(pos: Int, localSongModel: LovePhotoModel)
        fun onUpdateCount()
    }


}
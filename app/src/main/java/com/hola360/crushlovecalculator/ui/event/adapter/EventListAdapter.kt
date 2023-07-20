package com.hola360.crushlovecalculator.ui.event.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.data.model.event.EventModel
import com.hola360.crushlovecalculator.databinding.ItemCommonEventBinding
import com.hola360.crushlovecalculator.databinding.ItemMyEventBinding
import com.hola360.crushlovecalculator.utils.clickWithDebounce

@SuppressLint("NotifyDataSetChanged")
class EventListAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    val data = mutableListOf<EventModel>()
    var onMyEventClickListener: OnMyEventItemClickListener? = null

    @JvmName("setOnMyEventClickListener1")
    fun setOnMyEventClickListener(listener: OnMyEventItemClickListener) {
        onMyEventClickListener = listener
    }

    fun updateData(eventList: List<EventModel>?) {
        if (!eventList.isNullOrEmpty()) {
            data.clear()
            data.addAll(eventList)
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }

    inner class CommonEventViewHolder(val binding: ItemCommonEventBinding) :
        BaseViewHolder(binding.root) {
        @SuppressLint("Range")
        override fun onBind(position: Int) {
            binding.apply {
                commonEventModel = data[position]
                tvDate.apply {
                    setTextColor(Color.parseColor(data[position].themeColor))
                    setShadowLayer(0f, 0f, 0f, Color.parseColor(data[position].themeColor))
                }
                tvTitle.apply {
                    setTextColor(Color.parseColor(data[position].themeColor))
                    setShadowLayer(0f, 0f, 0f, Color.parseColor(data[position].themeColor))
                }
                tvDayLeft.apply {
                    setTextColor(Color.parseColor(data[position].themeColor))
                    setShadowLayer(0f, 0f, 0f, Color.parseColor(data[position].themeColor))
                }

            }
        }
    }

    inner class MyEventViewHolder(val binding: ItemMyEventBinding) : BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            binding.apply {
                myEventModel = data[position]
                root.clickWithDebounce { onMyEventClickListener?.onItemClick(data[position]) }
                ivMore.clickWithDebounce(1000) {
                    onMyEventClickListener?.onMoreIconClick(ivMore, data[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == 0) {
            val itemBinding =
                ItemCommonEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            CommonEventViewHolder(itemBinding)
        } else {
            val itemBinding =
                ItemMyEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MyEventViewHolder(itemBinding)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position].calType == -1) {
            COMMON_EVENT
        } else {
            MY_EVENT
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    companion object {
        const val COMMON_EVENT = 0
        const val MY_EVENT = 1
    }

    interface OnMyEventItemClickListener {
        fun onItemClick(eventModel: EventModel)
        fun onMoreIconClick(view: View, eventModel: EventModel)
    }
}
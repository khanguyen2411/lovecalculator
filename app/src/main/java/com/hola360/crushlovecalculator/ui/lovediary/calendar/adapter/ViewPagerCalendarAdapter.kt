package com.hola360.crushlovecalculator.ui.lovediary.calendar.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.databinding.ItemPagerCalendarBinding
import com.hola360.crushlovecalculator.ui.lovediary.model.Date
import com.hola360.crushlovecalculator.ui.lovediary.model.Month
import java.util.*

@SuppressLint("NotifyDataSetChanged")
class ViewPagerCalendarAdapter(context: Context) : RecyclerView.Adapter<BaseViewHolder>(),
    CalendarAdapter.OnItemClickListener2 {

    val data = mutableListOf<Month>()
    lateinit var listener: CalendarAdapter.OnItemClickListener

    fun updateData(newData: List<Month>) {
        if (newData.isNotEmpty()) {
            data.clear()
            data.addAll(newData)
            notifyDataSetChanged()
        }
    }

    fun addNextMonth(list: List<Month>) {
        val oldSize = data.size
        data.addAll(list)
        notifyItemRangeInserted(oldSize, oldSize - 1)
    }

    fun addPreviousMonth(list: List<Month>) {
        data.addAll(0, list)
        notifyItemRangeInserted(0, list.size)
    }

    fun unSelect(isSelectToday: Boolean) {
        var f1 = false
        var f2 = false
        data.forEachIndexed { index, month ->
            month.days.forEach {
                if (it.isSelected) {
                    it.isSelected = false
                    notifyItemChanged(index)
                    f1 = true
                }
                if (it.isNowDate) {
                    it.isSelected = isSelectToday
                    notifyItemChanged(index)
                    f2 = true
                }
                if (f1 && f2) {
                    return@forEachIndexed
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemBinding =
            ItemPagerCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewPagerCalendarViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewPagerCalendarViewHolder(val binding: ItemPagerCalendarBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            val mAdapter = CalendarAdapter()
            binding.rvDetailCalendar.apply {
                layoutManager = object : GridLayoutManager(context, 7) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }

                adapter = mAdapter
                mAdapter.listener = listener
                mAdapter.listener2 = this@ViewPagerCalendarAdapter
                mAdapter.updateData(data[position].days)
            }
        }
    }

    override fun onItemClickListener(date: Date) {
        data.forEachIndexed { index, month ->
            month.days.forEach {
                if (it != date && it.isSelected) {
                    it.isSelected = false
                    notifyItemChanged(index)
                }
            }
        }
    }
}
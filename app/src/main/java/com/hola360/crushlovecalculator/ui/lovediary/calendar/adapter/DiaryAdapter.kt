package com.hola360.crushlovecalculator.ui.lovediary.calendar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.data.model.diary.DiaryModel
import com.hola360.crushlovecalculator.databinding.ItemDateDiaryBinding
import com.hola360.crushlovecalculator.utils.clickWithDebounce

@SuppressLint("NotifyDataSetChanged")
class DiaryAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    val data = mutableListOf<DiaryModel>()
    lateinit var listener: OnDiaryClickListener

    fun updateData(list: MutableList<DiaryModel>) {
        if (list.isNotEmpty()) {
            data.clear()
            data.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemBinding =
            ItemDateDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class DiaryViewHolder(private val binding: ItemDateDiaryBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            binding.apply {
                diaryModel = data[position]
                root.clickWithDebounce {
                    listener.onDiaryClickListener(data[position])
                }
            }
        }
    }

    interface OnDiaryClickListener{
        fun onDiaryClickListener(diaryModel: DiaryModel)
    }
}
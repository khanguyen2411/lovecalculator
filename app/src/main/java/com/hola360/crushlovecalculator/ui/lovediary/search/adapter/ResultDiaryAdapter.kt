package com.hola360.crushlovecalculator.ui.lovediary.search.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.data.model.diary.DiaryModel
import com.hola360.crushlovecalculator.databinding.ItemSearchDiaryBinding
import com.hola360.crushlovecalculator.databinding.ItemSearchDiaryDateBinding
import com.hola360.crushlovecalculator.utils.clickWithDebounce

@SuppressLint("NotifyDataSetChanged")
class ResultDiaryAdapter : RecyclerView.Adapter<BaseViewHolder>() {

    val data = mutableListOf<DiaryModel>()
    lateinit var listener : OnItemClickListener

    fun updateData(newData: MutableList<DiaryModel>) {
        if (newData.isNotEmpty()) {
            data.clear()
            data.addAll(newData)
            notifyDataSetChanged()
        }
    }

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }

    inner class DiaryViewHolder(val binding: ItemSearchDiaryBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            binding.apply {
                if (!data[position].isDate) {
                    diaryModel = data[position]
                }
                root.clickWithDebounce{
                    listener.onDiaryItemClickListener(data[position])
                }
            }
        }
    }

    inner class DateViewHolder(val binding: ItemSearchDiaryDateBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            binding.apply {
                if (data[position].isDate) {
                    diaryModel = data[position]
                }
            }
        }
    }

    companion object {
        const val DIARY_TYPE = 0
        const val DATE_TYPE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if(viewType == DATE_TYPE){
            val itemBinding =
                ItemSearchDiaryDateBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            DateViewHolder(itemBinding)
        } else {
            val itemBinding = ItemSearchDiaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            DiaryViewHolder(itemBinding)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position].isDate) {
            DATE_TYPE
        } else {
            DIARY_TYPE
        }
    }

    interface OnItemClickListener{
        fun onDiaryItemClickListener(diaryModel: DiaryModel)
    }
}
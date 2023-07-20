package com.hola360.crushlovecalculator.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.databinding.ItemImageStoreCoverBinding

abstract class BaseStoreAdapter :
    RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return if (viewType == COVER_TYPE) {
            createCoverHolder(parent)
        } else {
            createNormalViewHolder(parent)
        }
    }


    protected fun createCoverHolder(parent: ViewGroup): CoverViewHolder {
        val itemBinding = ItemImageStoreCoverBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CoverViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    inner class CoverViewHolder(private val binding: ItemImageStoreCoverBinding) :
        BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
        }
    }

    abstract fun isHeader(position: Int): Boolean
    abstract fun createNormalViewHolder(parent: ViewGroup): BaseViewHolder

    companion object {
        const val COVER_TYPE = 0
        const val NORMAL_TYPE = 1
    }

}

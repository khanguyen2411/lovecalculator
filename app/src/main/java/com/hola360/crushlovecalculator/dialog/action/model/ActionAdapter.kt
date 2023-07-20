package com.hola360.crushlovecalculator.dialog.action.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.databinding.ItemActionDialogBinding

class ActionAdapter(
    private val onActionClickListener: OnActionClickListener?
) : RecyclerView.Adapter<BaseViewHolder>() {
    private val actions = mutableListOf<ActionModel>()
    fun updateData(data: ArrayList<ActionModel>?){
        actions.clear()
        if (data!=null){
            actions.addAll(data)
        }
        notifyDataSetChanged()
    }

    inner class ActionHolder(val binding: ItemActionDialogBinding) : BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            binding.actionModel = actions[position]
            binding.myLayoutRoot.setOnClickListener {
                onActionClickListener?.onItemActionClick(position)
            }
        }
    }

    interface OnActionClickListener {
        fun onItemActionClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val binding =
            ItemActionDialogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActionHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        (holder as ActionHolder).onBind(position)
    }

    override fun getItemCount(): Int {
        return actions.size
    }

}
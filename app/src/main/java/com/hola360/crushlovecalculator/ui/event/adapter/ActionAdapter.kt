package com.hola360.crushlovecalculator.ui.event.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.hola360.crushlovecalculator.databinding.ItemActionPopupBinding
import com.hola360.crushlovecalculator.ui.event.popup.EventActionModel

class ActionAdapter(
    private val actions: MutableList<EventActionModel>,
    private val onActionListener: OnActionListener
) : BaseAdapter() {

    interface OnActionListener {
        fun onItemClickListener(position: Int)
    }

    override fun getCount(): Int {
        return actions.size
    }

    override fun getItem(p0: Int): Any {
        return actions[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val holder: EventActionViewHolder
        if (p1 == null) {
            val binding =
                ItemActionPopupBinding.inflate(LayoutInflater.from(p2!!.context), p2, false)
            holder = EventActionViewHolder(binding)
            holder.binding.root.tag = holder
        } else {
            holder = p1.tag as EventActionViewHolder
        }
        holder.bind(p0)
        return holder.binding.root
    }

    inner class EventActionViewHolder(val binding: ItemActionPopupBinding) {
        fun bind(position: Int) {
            binding.actionModel = actions[position]
            binding.root.setOnClickListener {
                try {
                    onActionListener.onItemClickListener(position)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }
}
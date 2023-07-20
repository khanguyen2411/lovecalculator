package com.hola360.crushlovecalculator.ui.lovetime.emoijconnect

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.data.model.EmoijConnect
import com.hola360.crushlovecalculator.databinding.ItemEmoijConnectBinding
import com.hola360.crushlovecalculator.utils.clickWithDebounce

class EmojiAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listEmoij = mutableListOf<EmoijConnect>()
    var onEmojiItemSelectedListener: OnEmojiItemSelectedListener? = null

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<EmoijConnect>?) {
        listEmoij.clear()
        if (!newData.isNullOrEmpty()) {
            listEmoij.addAll(newData)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemEmoijConnectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConnectEmoijViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ConnectEmoijViewHolder) {
            holder.bind(position)
        }
    }

    override fun getItemCount(): Int {
        return listEmoij.size
    }

    inner class ConnectEmoijViewHolder(private val binding: ItemEmoijConnectBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.emojiConnect = listEmoij[position]
            binding.myLayoutRoot.clickWithDebounce {
                onEmojiItemSelectedListener?.onSelected(position, listEmoij[position])
            }
        }
    }

    interface OnEmojiItemSelectedListener {
        fun onSelected(position: Int, emoijConnect: EmoijConnect)
    }

}
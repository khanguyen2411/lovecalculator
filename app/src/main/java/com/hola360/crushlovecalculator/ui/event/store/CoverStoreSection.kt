package com.hola360.crushlovecalculator.ui.event.store

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.response.imagestore.storeBg.CoverStore
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.clickWithDebounce
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters

class CoverStoreSection constructor(
    sectionParameters: SectionParameters,
    title: String,
    itemList: List<CoverStore>,
    _clickListener: ClickListener
) : Section(sectionParameters) {

    private val title = title
    private var itemData = itemList
    private val clickListener = _clickListener

    override fun getContentItemsTotal(): Int {
        if (itemData!!.size > 2)
            return 2
        else
            return itemData!!.size
    }

    override fun getItemViewHolder(view: View?): CoverStoreViewHolder? {
        return view?.let { CoverStoreViewHolder(it) }
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val coverStoreHolder = holder as CoverStoreViewHolder
        val bg = itemData!![position]
        coverStoreHolder.imageItem.let {
            Glide.with(it)
                .load(Constants.EVENT_COVER_SUB_URL + bg.thumb)
                .placeholder(R.drawable.ic_loading_event2)
                .error(R.drawable.ic_loading_event2).into(it)
        }
        coverStoreHolder.imageItem.clickWithDebounce {
            clickListener.onItemImageViewClicked(this,  holder.adapterPosition)
        }
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
        val headHolder = holder as HeaderViewHolder
        headHolder.title.setText(title)
        headHolder.layoutRoot.clickWithDebounce {
            clickListener.onItemHeaderViewClicked(
                this,  holder.adapterPosition/3
            )
        }
    }

    override fun getHeaderViewHolder(view: View?): HeaderViewHolder? {
        return view?.let { HeaderViewHolder(it) }
    }

    inner class CoverStoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val root: View = itemView
        val imageItem: ImageView = itemView.findViewById(R.id.image_cover_store)
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title_cover_store)
        val btnSeeAll: TextView = itemView.findViewById(R.id.btnSeeAll)
        val layoutRoot: View = itemView.findViewById(R.id.layoutRoot)
    }

    interface ClickListener {
        fun onItemHeaderViewClicked(section: CoverStoreSection, itemAdapterPosition: Int)
        fun onItemImageViewClicked(section: CoverStoreSection, itemAdapterPosition: Int)
    }
}
package com.hola360.crushlovecalculator.ui.event.eventdetail

import android.annotation.SuppressLint
import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.data.model.story.StoryModel
import com.hola360.crushlovecalculator.databinding.ItemStoryBinding
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.Utils
import com.hola360.crushlovecalculator.utils.clickWithDebounce

@SuppressLint("NotifyDataSetChanged")
class EventDetailAdapter(private val app: Application) :
    RecyclerView.Adapter<BaseViewHolder>() {

    val data = mutableListOf<StoryModel>()
    var imageLayoutManager: GridLayoutManager? = null
    var columns: Int = Constants.AT_LEAST_COLUMN
    private var _listener: OnItemClickListener? = null
    var imageStoryListener: StoryImageAdapter.OnPhotoStoryClickListener? = null
        set(value) {
            field = value
        }

    fun setLayoutManager(manager: GridLayoutManager) {
        imageLayoutManager = manager
    }

    fun setListener(listener: OnItemClickListener) {
        _listener = listener
    }

    fun setMaxColumns(columns: Int) {
        this.columns = columns
    }

    fun updateData(list: List<StoryModel>) {
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

    inner class StoryViewHolder(val binding: ItemStoryBinding) : BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            binding.apply {
                storyModel = data[position]
                rvImages.apply {
                    val mAdapter = StoryImageAdapter(columns, false)
                    mAdapter.listener = imageStoryListener
                    adapter = mAdapter
                    val mLayoutManager = object : GridLayoutManager(app, columns) {
                        override fun canScrollVertically(): Boolean {
                            return false
                        }
                    }
                    layoutManager = mLayoutManager
                    mAdapter.updateData(Utils.jsonToList(data[position].images))
                }
                ivMore.clickWithDebounce {
                    _listener!!.onMoreIconClick(ivMore, data[position])
                }
                root.clickWithDebounce {
                    _listener?.onRootItemClick(data[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemStoryBinding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(itemStoryBinding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface OnItemClickListener {
        fun onMoreIconClick(view: View, storyModel: StoryModel)
        fun onRootItemClick(storyModel: StoryModel)
    }
}
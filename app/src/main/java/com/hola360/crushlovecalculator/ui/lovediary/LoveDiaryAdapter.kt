package com.hola360.crushlovecalculator.ui.lovediary

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hola360.crushlovecalculator.base.baseviewholder.BaseViewHolder
import com.hola360.crushlovecalculator.data.model.diary.DiaryModel
import com.hola360.crushlovecalculator.data.model.story.StoryModel
import com.hola360.crushlovecalculator.databinding.ItemDiaryBinding
import com.hola360.crushlovecalculator.databinding.ItemStoryBinding
import com.hola360.crushlovecalculator.ui.event.eventdetail.StoryImageAdapter
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.Utils
import com.hola360.crushlovecalculator.utils.clickWithDebounce

class LoveDiaryAdapter(private val app: Application) : RecyclerView.Adapter<BaseViewHolder>() {

    val data = mutableListOf<DiaryModel>()

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

    fun updateData(list: List<DiaryModel>) {
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

    inner class DiaryViewHolder(val binding: ItemDiaryBinding) : BaseViewHolder(binding.root) {
        override fun onBind(position: Int) {
            binding.apply {
                diaryModel = data[position]
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
        val itemDiaryBinding =
            ItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(itemDiaryBinding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface OnItemClickListener {
        fun onMoreIconClick(view: View, diaryModel: DiaryModel)
        fun onRootItemClick(diaryModel: DiaryModel)
    }
}
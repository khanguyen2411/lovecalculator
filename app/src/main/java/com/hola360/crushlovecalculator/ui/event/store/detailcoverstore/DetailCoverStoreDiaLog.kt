package com.hola360.crushlovecalculator.ui.event.store.detailcoverstore

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseViewModelDialogFragment
import com.hola360.crushlovecalculator.data.response.imagestore.storeBg.ListCoverStores
import com.hola360.crushlovecalculator.databinding.LayoutDetailCoverStoreDialogBinding
import com.hola360.crushlovecalculator.ui.event.store.detailsetbackgound.DetailSetBgDialog
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter

class DetailCoverStoreDiaLog : BaseViewModelDialogFragment<LayoutDetailCoverStoreDialogBinding>(),DetailCoverStoreSection.ClickListener, DetailSetBgDialog.OnSetBgEvent {
    private var listCoverStores: ListCoverStores? = null
    var sectionAdapter = SectionedRecyclerViewAdapter()
     var onclicklisterners : OnLickItemDetaiImageCover? = null
    override fun initView() {
        listCoverStores = requireArguments().getParcelable(KEY_LIST)
        val sectionParameters = SectionParameters.builder().itemResourceId(R.layout.item_image_cover_store).headerResourceId(
            R.layout.item_title_detail_list).build()
        val storeSection = DetailCoverStoreSection(sectionParameters, listCoverStores!!.title, listCoverStores!!.list, this)
        sectionAdapter.addSection(storeSection)
        binding!!.toolbar.setNavigationOnClickListener { dismiss() }
        binding!!.toolbar.title = listCoverStores!!.title
        val glm = GridLayoutManager(requireContext(), 2)
        glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (sectionAdapter.getSectionItemViewType(position) == SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER) {
                   2
                } else 1
            }
        }

        binding!!.recycleview.layoutManager = glm
        binding!!.recycleview.setHasFixedSize(true)
        binding!!.recycleview.adapter = sectionAdapter
    }

    override fun getLayout(): Int {
        return R.layout.layout_detail_cover_store_dialog
    }

    override fun initViewModel() {

    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }

    companion object {
        const val KEY_LIST = "key_list"
        fun create(listCoverStores: ListCoverStores): DetailCoverStoreDiaLog {
            val bundle = Bundle()
            bundle.putParcelable(KEY_LIST, listCoverStores)
            val dialog = DetailCoverStoreDiaLog()
            dialog.arguments = bundle
            return dialog
        }
    }

    interface OnLickItemDetaiImageCover{
        fun onLickItemImageCover(full :String)
    }
    override fun onItemRootViewClicked(section: DetailCoverStoreSection, itemAdapterPosition: Int) {
        var dialog = DetailSetBgDialog.create(listCoverStores!!.list[itemAdapterPosition-1].thumb)
        dialog.onlickListerner = this
        if(!dialog.isAdded){
            dialog.show(parentFragmentManager,"detailbg")
        }
    }

    override fun onSetBgEvent(full: String) {
        onclicklisterners!!.onLickItemImageCover(full)
        dismiss()
    }
}
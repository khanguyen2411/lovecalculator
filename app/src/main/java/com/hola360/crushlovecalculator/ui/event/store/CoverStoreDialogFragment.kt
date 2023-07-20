package com.hola360.crushlovecalculator.ui.event.store

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseViewModelDialogFragment
import com.hola360.crushlovecalculator.base.basefragment.BaseViewModelFragment
import com.hola360.crushlovecalculator.data.response.imagestore.storeBg.ListCoverStores
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.databinding.LayoutCoverStoreDialogFragmentBinding
import com.hola360.crushlovecalculator.ui.event.store.detailcoverstore.DetailCoverStoreDiaLog
import com.hola360.crushlovecalculator.ui.event.store.detailsetbackgound.DetailSetBgDialog
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter

class CoverStoreDialogFragment :
    BaseViewModelDialogFragment<LayoutCoverStoreDialogFragmentBinding>(),
    CoverStoreSection.ClickListener, DetailSetBgDialog.OnSetBgEvent,DetailCoverStoreDiaLog.OnLickItemDetaiImageCover {
    lateinit var viewModel: CoverStoreDiaLogFragmentViewModel
    private var countSize = -1
    var onclickListerner: OnClickItem? = null
    private val listCoverStore: MutableList<ListCoverStores> = mutableListOf()
    var sectionAdapter = SectionedRecyclerViewAdapter()
    val sectionParameters =
        SectionParameters.builder().itemResourceId(R.layout.item_image_cover_store)
            .headerResourceId(R.layout.item_title_cover_store).build()

    override fun initView() {
        binding!!.textTabToRetry.setOnClickListener(){
            viewModel.fetchData(false)
        }
        binding!!.toolbar.setNavigationOnClickListener { dismiss() }
        loadAdapter()
        binding!!.mySwipeRefreshLayout.setOnRefreshListener {
            binding!!.mySwipeRefreshLayout.isEnabled = true
            binding!!.mySwipeRefreshLayout.isRefreshing = true
            viewModel.fetchData(false)
        }
        binding!!.viewModel = viewModel
        viewModel.fetchData(true)
    }

    fun loadAdapter() {
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
        return R.layout.layout_cover_store_dialog_fragment
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }

    override fun initViewModel() {
        val factory =
            CoverStoreDiaLogFragmentViewModel.Factory(requireActivity().application as Application)
        viewModel = ViewModelProvider(this, factory)[CoverStoreDiaLogFragmentViewModel::class.java]
        viewModel.uiData.observe(this) {
            it?.let {
                when (it.loadDataStatus) {
                    LoadDataStatus.SUCCESS -> {
                        binding!!.mySwipeRefreshLayout.isEnabled = true
                        binding!!.mySwipeRefreshLayout.isRefreshing = false
                        val listStore = (it as DataResponse.DataSuccessResponse).body
                        if (countSize < listStore.size) {
                            for (i in 0..listStore.size - 1) {
                                val storeSection = CoverStoreSection(
                                    sectionParameters,
                                    listStore[i].title,
                                    listStore[i].list,
                                    this
                                )
                                sectionAdapter.addSection(storeSection)
                            }
                        }
                        if (countSize < listCoverStore.size) {
                            for (i in 0..listStore.size - 1) {
                                listCoverStore!!.add(listStore[i])
                            }
                        }
                        countSize = listStore.size
                        loadAdapter()
                    }
                    else -> {
                        binding!!.mySwipeRefreshLayout.isEnabled = true
                        binding!!.mySwipeRefreshLayout.isRefreshing = false
                    }
                }
            }
        }
    }

    companion object {
        fun create(): CoverStoreDialogFragment {
            return CoverStoreDialogFragment()
        }
    }

    override fun onItemHeaderViewClicked(section: CoverStoreSection, itemAdapterPosition: Int) {
        val seeAllDialogFragment = DetailCoverStoreDiaLog.create(listCoverStore[itemAdapterPosition])
        seeAllDialogFragment.onclicklisterners = this
        seeAllDialogFragment.show(parentFragmentManager, "storeCoverDialog")
    }

    override fun onItemImageViewClicked(section: CoverStoreSection, itemAdapterPosition: Int) {
        var dialog = DetailSetBgDialog.create(listCoverStore[itemAdapterPosition / 3].list[itemAdapterPosition % 3 - 1].thumb)
        dialog.onlickListerner = this
        if (!dialog.isAdded) {
            dialog.show(parentFragmentManager, "detailbg")
        }
    }

    interface OnClickItem {
        fun onClickItemImage(full: String)
    }

    override fun onSetBgEvent(full: String) {
        onclickListerner!!.onClickItemImage(full)
        dismiss()
    }

    override fun onLickItemImageCover(full: String) {
        onclickListerner!!.onClickItemImage(full)
        dismiss()
    }

}
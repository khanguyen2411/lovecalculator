package com.hola360.crushlovecalculator.ui.myphoto.detail

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.LovePhotoModel
import com.hola360.crushlovecalculator.databinding.FragmentDetailPhotoBinding
import com.hola360.crushlovecalculator.ui.myphoto.base.BasePhotoFragment
import com.hola360.crushlovecalculator.ui.myphoto.adapter.DetailPhotoAdapter

class DetailPhotoFragment : BasePhotoFragment<DetailPhotoViewModel, FragmentDetailPhotoBinding>() {
    private val mAdapter = DetailPhotoAdapter()
    private val args: DetailPhotoFragmentArgs by navArgs()
    private var curPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        curPosition = args.curPos
        isOldestDateFirst = args.oldestDateFirst
    }


    override fun initView() {
        binding!!.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding!!.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_share) {
                share()
            } else if (item.itemId == R.id.action_delete) {
                deleteFiles()
            }
            true
        }
        binding!!.vpPhoto.apply {
            adapter = mAdapter
        }
        binding!!.viewModel = viewModel
        viewModel!!.fetchAllPhotos(mainActivity, isOldestDateFirst, true)
    }


    override fun getLayout(): Int {
        return R.layout.fragment_detail_photo
    }

    override fun initViewModel() {
        val factory = DetailPhotoViewModel.Factory(mainActivity.application)
        viewModel = ViewModelProvider(this, factory)[DetailPhotoViewModel::class.java]
        observeData()
    }

    override fun getSelectedItem() {
        selectedItemList.clear()
        selectedItemList.add(mAdapter.photos[binding!!.vpPhoto.currentItem])
    }

    override fun onDeleteSuccess() {
        curPosition = binding!!.vpPhoto.currentItem
    }

    override fun onCancelDelete() {
    }

    override fun onFetchDataSuccess(data: MutableList<LovePhotoModel>) {
        mAdapter.updateData(data)
        binding!!.vpPhoto.setCurrentItem(curPosition.coerceAtMost(data.size - 1), false)
    }

    override fun onFetchDataError() {
        findNavController().navigateUp()
    }
}
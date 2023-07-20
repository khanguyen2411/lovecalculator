package com.hola360.crushlovecalculator.ui.event.base

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basefragment.BaseViewModelFragment
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.databinding.FragmentListEventBinding
import com.hola360.crushlovecalculator.ui.event.adapter.EventListAdapter

abstract class BaseEventFragment<V : BaseEventViewModel> :
    BaseViewModelFragment<FragmentListEventBinding>() {

    val mAdapter = EventListAdapter()
    private lateinit var mLayoutManager: LinearLayoutManager
    protected lateinit var viewModel: V

    override fun getLayout(): Int {
        return R.layout.fragment_list_event
    }

    override fun initView() {
        initLayoutManager()
        binding!!.apply {
            rvEvent.apply {
                adapter = mAdapter
                layoutManager = mLayoutManager
            }
            mViewModel = viewModel
        }
    }

    private fun initLayoutManager() {
        mLayoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
    }

    fun initObserveData() {
        viewModel.uiData.observe(this) {
            it.let {
                when (it.loadDataStatus) {
                    LoadDataStatus.SUCCESS -> {
                        val eventList = (it as DataResponse.DataSuccessResponse).body
                        mAdapter.updateData(eventList)
                    }
                    LoadDataStatus.ERROR -> {
                        mAdapter.clearData()
                    }
                    else -> {}
                }
            }
        }
        viewModel.fetchData()
    }
}
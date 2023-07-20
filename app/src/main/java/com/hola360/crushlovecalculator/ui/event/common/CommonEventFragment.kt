package com.hola360.crushlovecalculator.ui.event.common

import androidx.lifecycle.ViewModelProvider
import com.hola360.crushlovecalculator.ui.event.base.BaseEventFragment

class CommonEventFragment : BaseEventFragment<CommonEventViewModel>() {
    override fun initViewModel() {
        val factory = CommonEventViewModel.Factory(mainActivity.application)
        viewModel = ViewModelProvider(this, factory)[CommonEventViewModel::class.java]
        initObserveData()
    }

    override fun initView() {
        super.initView()
        binding!!.tvRetryOrAdd.setOnClickListener{
            viewModel.retry()
        }
    }
}
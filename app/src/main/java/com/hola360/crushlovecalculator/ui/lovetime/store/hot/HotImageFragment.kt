package com.hola360.crushlovecalculator.ui.lovetime.store.hot

import androidx.lifecycle.ViewModelProvider
import com.hola360.crushlovecalculator.ui.lovetime.store.base.BaseStorePageFragment
import com.hola360.crushlovecalculator.ui.lovetime.store.newbg.NewImageViewModel

class HotImageFragment : BaseStorePageFragment<HotImageViewModel>() {
    override fun initViewModel() {
        val factory = HotImageViewModel.Factory(mainActivity.application)
        viewModel = ViewModelProvider(this, factory)[HotImageViewModel::class.java]
        initObserveData()
    }
}
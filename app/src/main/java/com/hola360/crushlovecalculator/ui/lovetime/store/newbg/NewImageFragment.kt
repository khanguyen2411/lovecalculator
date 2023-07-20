package com.hola360.crushlovecalculator.ui.lovetime.store.newbg

import androidx.lifecycle.ViewModelProvider
import com.hola360.crushlovecalculator.ui.lovetime.store.base.BaseStorePageFragment

class NewImageFragment : BaseStorePageFragment<NewImageViewModel>() {
    override fun initViewModel() {
        val factory = NewImageViewModel.Factory(mainActivity.application)
        viewModel = ViewModelProvider(this, factory)[NewImageViewModel::class.java]
        initObserveData()
    }

}
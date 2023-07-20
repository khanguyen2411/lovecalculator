package com.hola360.crushlovecalculator.ui.lovetime.store.favorite

import androidx.lifecycle.ViewModelProvider
import com.hola360.crushlovecalculator.ui.lovetime.store.base.BaseStorePageFragment

class FavoriteImageFragment : BaseStorePageFragment<FavoriteImageViewModel>() {
    override fun initViewModel() {
        val factory = FavoriteImageViewModel.Factory(mainActivity.application)
        viewModel = ViewModelProvider(this, factory)[FavoriteImageViewModel::class.java]
        initObserveData()
    }

}
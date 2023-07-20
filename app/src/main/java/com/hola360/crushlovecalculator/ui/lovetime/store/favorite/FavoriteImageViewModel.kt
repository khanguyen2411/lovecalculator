package com.hola360.crushlovecalculator.ui.lovetime.store.favorite

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.data.utils.StoreDataResponse
import com.hola360.crushlovecalculator.ui.lovetime.store.base.BaseStoreViewModel
import kotlinx.coroutines.launch

class FavoriteImageViewModel(val app: Application) : BaseStoreViewModel(app) {
    class Factory(private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoriteImageViewModel::class.java)) {
                return FavoriteImageViewModel(application) as T
            }
            throw  IllegalArgumentException(application.resources.getString(R.string.unable_create_viewmodel))
        }
    }

    override fun fetchData(isMore: Boolean) {
        viewModelScope.launch {
            val allFavorite = repository.getAllFavorites()
            if (allFavorite == null || allFavorite.isEmpty()) {
                uiData.value = StoreDataResponse.DataErrorResponse(0)
            } else {
                uiData.value = StoreDataResponse.DataSuccessResponse(allFavorite, 0)
            }
        }
    }

    override fun checkEmptyData(): Boolean {
        return (uiData.value!!.loadDataStatus == LoadDataStatus.ERROR)
    }

    override fun isShowRetry(): Boolean {
        return false
    }

    override fun emptyMessage(): String {
        return app.getString(R.string.empty_favorites_list)
    }

    override fun emptyTitle(): String {
        return "No Favorite Images"
    }
}
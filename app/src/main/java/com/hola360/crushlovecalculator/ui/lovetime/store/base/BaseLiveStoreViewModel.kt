package com.hola360.crushlovecalculator.ui.lovetime.store.base

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.app.App
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.data.utils.StoreDataResponse
import com.hola360.crushlovecalculator.utils.Constants
import kotlinx.coroutines.launch
import java.lang.NullPointerException

abstract class BaseLiveStoreViewModel(val app: Application, val isNew: Boolean) :
    BaseStoreViewModel(app) {
    protected var totalPage = 0
    protected var curPage = 0
    override fun fetchData(isMore: Boolean) {
        if (uiData.value!!.loadDataStatus != LoadDataStatus.LOADING && uiData.value!!.loadDataStatus != LoadDataStatus.LOADING_MORE && uiData.value!!.loadDataStatus != LoadDataStatus.REFRESH) {
            if (!isMore) {
                if (totalPage == 0 && curPage == 0) {
                    uiData.value = StoreDataResponse.DataLoading(LoadDataStatus.LOADING)
                } else {
                    curPage = 0
                    totalPage = 0
                    uiData.value = StoreDataResponse.DataLoading(LoadDataStatus.REFRESH)
                }
            } else {
                if (curPage > totalPage) return
                uiData.value = StoreDataResponse.DataLoading(LoadDataStatus.LOADING_MORE)
            }

            viewModelScope.launch {
                val requestPage = if (totalPage > 0) {
                    if (uiData.value!!.loadDataStatus == LoadDataStatus.REFRESH) {
                        1
                    } else {
                        (curPage + 1)
                    }
                } else {
                    1
                }
                val imageStoreData = repository.getImageStore(
                    if (isNew) {
                        "new"
                    } else {
                        "hot"
                    },
                    requestPage,
                    Constants.ROWS_PER_PAGE * (app as App).photoColumns
                )
                if (imageStoreData != null) {
                    try {
                        val storeList = imageStoreData.data?.dataApps?.lists
                        totalPage = imageStoreData.data?.dataApps?.totalPage!!
                        curPage = imageStoreData.data.dataApps.currentPage
                        if (storeList != null && storeList.isNotEmpty()) {
                            storeList.map {
                                it.full = imageStoreData.data.dataApps.subUrl.plus(it.full)
                                it.thumb = imageStoreData.data.dataApps.subUrl.plus(it.thumb)
                            }
                            uiData.value = StoreDataResponse.DataSuccessResponse(storeList, curPage)
                        } else {
                            uiData.value = StoreDataResponse.DataErrorResponse(curPage)
                        }
                    } catch (ex: NullPointerException) {
                        uiData.value = StoreDataResponse.DataErrorResponse(curPage)
                    }
                } else {
                    uiData.value = StoreDataResponse.DataErrorResponse(curPage)
                }

            }
        }
    }

    override fun checkEmptyData(): Boolean {
        return (uiData.value!!.loadDataStatus == LoadDataStatus.ERROR) && (curPage < 1)
    }

    override fun isShowRetry(): Boolean {
        return true
    }

    override fun emptyMessage(): String {
        return app.getString(R.string.string_lovetime_emoij_you_offline)
    }

    override fun emptyTitle(): String {
        return app.getString(R.string.string_lovetime_emoij_no_internet)
    }
}
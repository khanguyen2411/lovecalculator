package com.hola360.crushlovecalculator.data.utils


sealed class StoreDataResponse<T> constructor(val loadDataStatus: LoadDataStatus) {
    class DataEmptyResponse<T> : StoreDataResponse<T>(LoadDataStatus.IDLE)
    class DataErrorResponse<T>(val curPage: Int) : StoreDataResponse<T>(LoadDataStatus.ERROR)
    data class DataSuccessResponse<T>(val body: T,val curPage: Int) : StoreDataResponse<T>(LoadDataStatus.SUCCESS)
    class DataLoading<T>(val loadingType: LoadDataStatus) : StoreDataResponse<T>(loadingType)
}
package com.hola360.crushlovecalculator.data.utils


sealed class DataResponse<T> constructor(val loadDataStatus: LoadDataStatus) {
    class DataEmptyResponse<T> : DataResponse<T>(LoadDataStatus.IDLE)
    class DataErrorResponse<T>(val throwable: Exception?) : DataResponse<T>(LoadDataStatus.ERROR)
    data class DataSuccessResponse<T>(val body: T) : DataResponse<T>(LoadDataStatus.SUCCESS)
    class DataLoadingResponse<T> : DataResponse<T>(LoadDataStatus.LOADING)
}
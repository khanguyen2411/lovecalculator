package com.hola360.crushlovecalculator.data.utils


sealed class DownloadResponse<T> constructor(val loadDataStatus: LoadDataStatus) {
    class DataEmptyResponse<T> : DownloadResponse<T>(LoadDataStatus.IDLE)
    class DataErrorResponse<T>(val throwable: Exception?) : DownloadResponse<T>(LoadDataStatus.ERROR)
    data class DataSuccessResponse<T>(val body: T) : DownloadResponse<T>(LoadDataStatus.SUCCESS)
    class DataLoadingResponse<T>(val progress: Int) : DownloadResponse<T>(LoadDataStatus.LOADING)
}
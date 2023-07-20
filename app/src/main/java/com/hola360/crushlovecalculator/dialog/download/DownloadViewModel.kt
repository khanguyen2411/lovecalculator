package com.hola360.crushlovecalculator.dialog.download

import android.app.Application
import androidx.lifecycle.*
import com.hola360.crushlovecalculator.app.App
import com.hola360.crushlovecalculator.data.CountType
import com.hola360.crushlovecalculator.data.repository.ImageStoreRepository
import com.hola360.crushlovecalculator.data.utils.DownloadResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.dialog.download.model.DownloadModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.*
import java.util.concurrent.TimeUnit

class DownloadViewModel(app: Application, val downloadModel: DownloadModel) : ViewModel() {
    val repository = ImageStoreRepository(app)
    val downloadStatusLiveData = MutableLiveData<DownloadResponse<Boolean>>(DownloadResponse.DataEmptyResponse())
    val isLoading: LiveData<Boolean> = Transformations.map(downloadStatusLiveData) {
        downloadStatusLiveData.value!!.loadDataStatus == LoadDataStatus.LOADING
    }

    fun downloadFile() {
        viewModelScope.launch {
            viewModelScope.launch {
                downloadStatusLiveData.value = (DownloadResponse.DataLoadingResponse(0))
            }

            val okHttpClient: OkHttpClient = OkHttpClient.Builder().readTimeout((10 * 1000).toLong(), TimeUnit.MILLISECONDS).build()

            withContext(Dispatchers.IO) {
                var input: InputStream? = null
                val output: OutputStream = FileOutputStream(File(downloadModel.outputPath))

                try {
                    val request: Request = Request.Builder()
                        .url(downloadModel.url)
                        .build()
                    val response: Response = okHttpClient.newCall(request).execute()

                    if (response.isSuccessful) {
                        input = response.body.byteStream()
                        val tLength = response.body.contentLength()
                        val data = ByteArray(1024)
                        var total: Long = 0
                        var count: Int

                        while (input.read(data).also { count = it } != -1) {
                            total += count.toLong()
                            viewModelScope.launch {
                                if (tLength > 0) {
                                    downloadStatusLiveData.value =
                                        (DownloadResponse.DataLoadingResponse((total * 100 / tLength).toInt()))
                                }
                            }
                            output.write(data, 0, count)
                        }

                        output.flush()
                        output.close()
                        input.close()

                        viewModelScope.launch {
                            downloadStatusLiveData.value =
                                (DownloadResponse.DataLoadingResponse(100))
                        }

                        if (downloadModel.itemId != null) {
                            repository.pushCount(downloadModel.itemId, CountType.Download, true)
                        }

                        viewModelScope.launch {
                            downloadStatusLiveData.value =
                                (DownloadResponse.DataSuccessResponse(true))
                        }
                    }
                } catch (e: Exception) {
                    viewModelScope.launch {
                        downloadStatusLiveData.value = (DownloadResponse.DataErrorResponse(null))
                    }
                } finally {
                    if (input != null) {
                        try {
                            input.close()
                        } catch (ioe: IOException) { }
                    }

                    try {
                        output.close()
                    } catch (e: IOException) { }
                }
            }
        }
    }

    class Factory(private val app: App, val downloadModel: DownloadModel) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DownloadViewModel::class.java)) {
                return DownloadViewModel(app, downloadModel) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}
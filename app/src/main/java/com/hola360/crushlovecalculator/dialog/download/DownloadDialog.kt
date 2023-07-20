package com.hola360.crushlovecalculator.dialog.download

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.app.App
import com.hola360.crushlovecalculator.base.basedialog.BaseAlertDialog
import com.hola360.crushlovecalculator.data.utils.DownloadResponse
import com.hola360.crushlovecalculator.databinding.DialogDownloadBinding
import com.hola360.crushlovecalculator.dialog.download.model.DownloadModel
import com.hola360.crushlovecalculator.utils.ToastUtils


class DownloadDialog : BaseAlertDialog<DialogDownloadBinding>() {
    private lateinit var mViewModel: DownloadViewModel
    private var downloadModel: DownloadModel? = null
    var onDownloadFileListener: OnDownloadFileListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        downloadModel = requireArguments().getParcelable(KEY_DATA)
        if (downloadModel == null) {
            dismiss()
            return
        }
        initViewModel()
    }

    private fun initViewModel() {
        val factory =
            DownloadViewModel.Factory(requireActivity().application as App, downloadModel!!)
        mViewModel = ViewModelProvider(this, factory)[DownloadViewModel::class.java]
        mViewModel.downloadStatusLiveData.observe(this) {
            it?.let {
                when (it) {
                    is DownloadResponse.DataLoadingResponse -> {
                        val progress = it.progress
                        binding!!.progressBar.progress = progress
                        binding!!.tvPercentage.text =
                            getString(R.string.msg_downloading, "${progress}%")
                    }
                    is DownloadResponse.DataSuccessResponse -> {
                        dismiss()
                        onDownloadFileListener?.onDownloadedSuccess(downloadModel?.itemId)
                    }
                    is DownloadResponse.DataErrorResponse -> {
                        ToastUtils.getInstance(mainActivity)
                            .showToast(getString(R.string.msg_download_failed))
                    }
                }
            }
        }
    }


    override fun getLayout(): Int {
        return R.layout.dialog_download
    }

    override fun initView() {
        mDialog!!.setCancelable(false)
        isCancelable = false
        binding!!.imClose.setOnClickListener { dismiss() }
        binding!!.myButtonDownload.setOnClickListener {
            mViewModel.downloadFile()
        }
        binding!!.viewModel = mViewModel
    }

    interface OnDownloadFileListener {
        fun onDownloadedSuccess()
        fun onDownloadedSuccess(itemId: String?)
    }

    companion object {
        const val KEY_DATA = "KEY_Data"
        private var instance : DownloadDialog? = null

        fun create(): DownloadDialog {
            return if(instance != null){
                instance!!
            } else {
                DownloadDialog()
            }
        }
    }
}
package com.hola360.crushlovecalculator.ui.lovetime.store.detail

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseViewModelDialogFragment
import com.hola360.crushlovecalculator.data.CountType
import com.hola360.crushlovecalculator.data.model.ImageStore
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.databinding.DialogDetailPhotoBinding
import com.hola360.crushlovecalculator.dialog.LoadingDialog
import com.hola360.crushlovecalculator.utils.SharedPreferenceUtils
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.clickWithDebounce

class ImageStoreDetailDialog : BaseViewModelDialogFragment<DialogDetailPhotoBinding>() {
    private var viewModel: DetailImageViewModel? = null
    var onSelectImageListener: OnSelectImageListener? = null
    private var imageStore: ImageStore? = null

    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)

    }

    private fun showSubmitDialog() {
        loadingDialog.show(parentFragmentManager, "SubmittingDlg")
    }

    private fun dismissSubmitDialog() {
        loadingDialog.dismiss()
    }

    override fun getLayout(): Int {
        return R.layout.dialog_detail_photo
    }

    override fun initView() {
        binding!!.toolbar.setNavigationOnClickListener { onBackPress() }
        binding!!.viewModel = viewModel
        binding!!.btShare.clickWithDebounce {
            SystemUtils.shareUrl(mainActivity, imageStore!!.full!!)
        }
        binding!!.btSetAs.setOnClickListener {
            viewModel!!.pushDownload()
        }
        viewModel!!.checkFavorite()
    }

    private fun onBackPress() {
        dismiss()
        onSelectImageListener?.onDismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setOnKeyListener { dialog, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                onBackPress()
            }
            true
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }

    interface OnSelectImageListener {
        fun onSelected()
        fun onDismiss()
    }

    companion object {
        private const val KEY_DATA = "Key_DATA"
        fun create(imageStore: ImageStore): ImageStoreDetailDialog {
            val bundle = Bundle()
            bundle.putParcelable(KEY_DATA, imageStore)
            val dialog = ImageStoreDetailDialog()
            dialog.arguments = bundle
            return dialog
        }
    }

    override fun initViewModel() {
        imageStore = requireArguments().getParcelable(KEY_DATA)
        if (imageStore == null) {
            dismiss()
        }

        val factory = DetailImageViewModel.Factory(mainActivity.application, imageStore!!)
        viewModel = ViewModelProvider(this, factory)[DetailImageViewModel::class.java]
        viewModel!!.uiData.observe(this) {
            it?.let {
                viewModel!!.pushCount(CountType.View)
            }
        }
        viewModel!!.pushFavoriteLiveData.observe(this) {
            it?.let {
                if (it is DataResponse.DataLoadingResponse) {
                    showSubmitDialog()
                } else {
                    if (it !is DataResponse.DataEmptyResponse) {
                        dismissSubmitDialog()
                    }
                }
            }
        }
        viewModel!!.pushDownloadLiveData.observe(this) {
            it?.let {
                if (it is DataResponse.DataLoadingResponse) {
                    showSubmitDialog()
                } else {
                    if (it !is DataResponse.DataEmptyResponse) {
                        dismissSubmitDialog()
                        dismiss()
                        SharedPreferenceUtils.getInstance(mainActivity)
                            .setLoveBg(imageStore!!.full!!)
                        onSelectImageListener?.onSelected()
                    }
                }
            }
        }
    }
}
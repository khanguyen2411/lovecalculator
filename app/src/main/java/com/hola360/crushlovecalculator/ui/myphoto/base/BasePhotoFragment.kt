package com.hola360.crushlovecalculator.ui.myphoto.base

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.databinding.ViewDataBinding
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basefragment.BaseViewModelFragment
import com.hola360.crushlovecalculator.data.LovePhotoModel
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.dialog.MessageAlertDialog
import com.hola360.crushlovecalculator.ui.myphoto.detail.DetailPhotoFragment
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.snackbar.SnackBarType


abstract class BasePhotoFragment<T : BasePhotoViewModel, V : ViewDataBinding> :
    BaseViewModelFragment<V>() {
    protected var isOldestDateFirst = false
    protected var viewModel: T? = null
    protected val selectedItemList = mutableListOf<LovePhotoModel>()


    protected fun showMessageDialog(list: List<LovePhotoModel>) {
        val message = resources.getQuantityString(R.plurals.msg_delete_file, list.size, list.size)
        val title = getString(R.string.delete_title)
        val messageAlertDialog = MessageAlertDialog.create(title, message)
        messageAlertDialog.onAlertMessageDialogClickListener =
            object : MessageAlertDialog.OnAlertMessageDialogClickListener {
                override fun onPositive() {
                    doDeletePhotos()
                }

                override fun onNegative() {
                    onCancelDelete()
                }
            }
        messageAlertDialog.show(parentFragmentManager, "DeletePhotos")
    }

    protected fun startDeleteList(list: List<LovePhotoModel>) {
        if (SystemUtils.isAndroidR()) {
            val intentSender: IntentSender = getIntentSender(list)
            launchIntentSender(intentSender)
        } else {
            showMessageDialog(list)
        }
    }

    protected fun launchIntentSender(intentSender: IntentSender) {
        val intentSenderRequest = IntentSenderRequest.Builder(intentSender)
            .build()
        deleteRequest.launch(intentSenderRequest)

    }

    private val deleteRequest = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result != null && result.resultCode == Activity.RESULT_OK) {
            if (selectedItemList.isNotEmpty()) {
                doDeletePhotos()
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    private fun getIntentSender(list: List<LovePhotoModel>): IntentSender {
        val uris: MutableList<Uri> = ArrayList()
        for (it in list) {
            uris.add(it.uri!!)
        }
        return MediaStore.createWriteRequest(
            mainActivity.contentResolver,
            uris
        ).intentSender
    }

    protected fun share() {
        getSelectedItem()
        if (selectedItemList.isNotEmpty()) {
            SystemUtils.shareMedia(mainActivity, selectedItemList.flatMap {
                val uris = mutableListOf<Uri>()
                uris.add(it.uri!!)
                uris
            }.toMutableList())
        } else {
            showEmptyListWarning()
        }

    }

    protected fun observeData() {
        viewModel!!.lovePhotoLiveData.observe(this) {
            it?.let {
                if (it.loadDataStatus == LoadDataStatus.SUCCESS) {
                    val data = (it as DataResponse.DataSuccessResponse).body
                    onFetchDataSuccess(data)
                } else if (it.loadDataStatus == LoadDataStatus.ERROR) {
                    onFetchDataError()
                }
            }
        }
        viewModel!!.deleteFilesLiveData.observe(this) {
            it?.let {
                if (it is DataResponse.DataSuccessResponse) {
                    onDeleteSuccess()
                    viewModel!!.fetchAllPhotos(
                        mainActivity,
                        isOldestDateFirst,
                        this@BasePhotoFragment is DetailPhotoFragment
                    )
                } else if (it is DataResponse.DataErrorResponse) {
                    if (SystemUtils.isAndroidQ()) {
                        val securityException = it.throwable as SecurityException
                        val recoverableSecurityException =
                            securityException as RecoverableSecurityException
                        launchIntentSender(recoverableSecurityException.userAction.actionIntent.intentSender)
                    }

                }
            }
        }
    }

    protected fun deleteFiles() {
        getSelectedItem()
        if (selectedItemList.isEmpty()) {
          showEmptyListWarning()
        } else {
            startDeleteList(selectedItemList)
        }
    }

    private fun showEmptyListWarning(){
        mainActivity.showSnackBar(
            SnackBarType.Warning,
            getString(R.string.title_warning),
            getString(R.string.empty_list)
        )
    }

    fun doDeletePhotos() {
        viewModel!!.deleteFiles(mainActivity, selectedItemList)
    }

    abstract fun onDeleteSuccess()
    abstract fun onFetchDataSuccess(data: MutableList<LovePhotoModel>)
    abstract fun onFetchDataError()
    abstract fun onCancelDelete()
    abstract fun getSelectedItem()


}
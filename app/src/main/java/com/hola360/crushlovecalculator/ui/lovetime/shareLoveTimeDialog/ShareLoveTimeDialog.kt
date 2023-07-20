package com.hola360.crushlovecalculator.ui.lovetime.shareLoveTimeDialog

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.Gallery
import com.hola360.crushlovecalculator.databinding.LayoutShareLoveTimeDialogBinding
import com.hola360.crushlovecalculator.ui.lovetime.LoveTimeViewModel
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.PhotoUtils
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.clickWithDebounce
import com.hola360.crushlovecalculator.utils.snackbar.SnackBarType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShareLoveTimeDialog : AbsBaseAlertDialog<LayoutShareLoveTimeDialogBinding>(),
    View.OnClickListener {
    private var viewModel: LoveTimeViewModel? = null
    private var resultPicture: Gallery? = null
    override fun initView() {
        binding!!.download.setOnClickListener(this)
        binding!!.viewModel = viewModel
        viewModel!!.fetchLoveTime(false)

        binding!!.share.clickWithDebounce {
            if (resultPicture != null) {
                PhotoUtils.shareSinglePhoto(requireActivity(), resultPicture!!)
            } else {
                getBitmapFromView(true)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.layout_share_love_time_dialog
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog!!.window!!.apply {
            setDimAmount(0.6F)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    companion object {
        fun create(): ShareLoveTimeDialog {
            return ShareLoveTimeDialog()
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.download -> {
                binding!!.download.visibility = View.GONE
                checkStoragePermission()
            }
        }
    }


    private fun checkStoragePermission() {
        if (!SystemUtils.hasPermissions(requireContext(), *SystemUtils.getStoragePermissions())) {
            storageResultLauncher.launch(SystemUtils.getStoragePermissions())
        } else {
            downloadPhoto()
        }
    }

    private val storageResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (SystemUtils.hasPermissions(
                    requireContext(),
                    *SystemUtils.getStoragePermissions()
                )
            ) {
                downloadPhoto()
            } else {
                SystemUtils.showAlertPermissionNotGrant(binding!!, requireActivity())
            }
        }

    private fun downloadPhoto() {
        getBitmapFromView(false)
    }


    @SuppressLint("UseRequireInsteadOfGet")
    private fun getBitmapFromView(share: Boolean) {

        CoroutineScope(Dispatchers.IO).launch {
            val v = binding!!.loveCard2
            v.let {
                val b = Bitmap.createBitmap(
                    v.width, v.height, Bitmap.Config.ARGB_8888
                )
                val c = Canvas(b)
                v.layout(v.left, v.top, v.right, v.bottom)
                v.draw(c)
                val gallery = PhotoUtils.savePhoto(requireContext(), Constants.PHOTO_PATH, b)
                withContext(Dispatchers.Main) {
                    if (gallery != null) {
                        resultPicture = gallery
                        if (share) {
                            shareLoveCard()
                        }
                        setupWhenPictureTaken()
                    }
                }
            }
        }
    }

    fun loadBitmapFromView(v: View): Bitmap? {
        val b = Bitmap.createBitmap(
            v.layoutParams.width,
            v.layoutParams.height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return b
    }

    private fun shareLoveCard() {
        PhotoUtils.shareSinglePhoto(requireActivity(), resultPicture!!)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun setupWhenPictureTaken() {
        showSuccessSnackBar()
    }

    private fun showSuccessSnackBar() {
        mainActivity.showSnackBar(
            SnackBarType.Success,
            getString(R.string.title_success),
            getString(R.string.love_test_result_download_success)
        )
    }

    override fun initViewModel() {
        val factory = LoveTimeViewModel.Factory(mainActivity.application)
        viewModel = ViewModelProvider(this, factory)[LoveTimeViewModel::class.java]
    }
}
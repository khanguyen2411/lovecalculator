package com.hola360.crushlovecalculator.dialog.crop

import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import com.canhub.cropper.CropImageView
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseFullAlertDialog
import com.hola360.crushlovecalculator.databinding.FragmentCropImageBinding

class CropPhotoDialog : BaseFullAlertDialog<FragmentCropImageBinding>() {
    private val width = Resources.getSystem().displayMetrics.widthPixels
    private val height = Resources.getSystem().displayMetrics.heightPixels
    private var isClicked = false
    private var uri: Uri? = null
    private var isFromMediaStore = false
    private var isSquare = false
    private var isRectangle = false
    var croppedHeight = 0
    var croppedWidth = 0
    var onCropDoneListener: OnCropDoneListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
        uri = requireArguments().getParcelable(KEY_URI)
        isFromMediaStore = requireArguments().getBoolean(KEY_FROM_MEDIA_STORE)
        isSquare = requireArguments().getBoolean(KEY_SQUARE)
        isRectangle = requireArguments().getBoolean(KEY_RECTANGLE)
        if (uri == null) {
            dismiss()
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_crop_image
    }

    override fun initView() {
        binding!!.toolbar.setNavigationOnClickListener { onBackPress() }
        binding!!.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_done) {
                if (!isClicked) {
                    if (croppedHeight != 0 && croppedWidth != 0) {
                        binding!!.cropView.croppedImageAsync(
                            reqHeight = croppedHeight,
                            reqWidth = croppedWidth
                        )
                    } else {
                        binding!!.cropView.croppedImageAsync()
                    }
                    isClicked = true
                }
            }
            true
        }

        binding!!.flipVertical.setOnClickListener { binding!!.cropView.flipImageVertically() }
        binding!!.flipHorizontal.setOnClickListener { binding!!.cropView.flipImageHorizontally() }
        binding!!.rotate.setOnClickListener { binding!!.cropView.rotateImage(ROTATE_DEGREES) }
        binding!!.cropView.maxZoom = 2
        binding!!.cropView.isAutoZoomEnabled
        binding!!.cropView.guidelines = CropImageView.Guidelines.ON
        binding!!.cropView.setImageUriAsync(uri)
        if (isSquare && !isRectangle) {
            binding!!.cropView.setAspectRatio(1, 1)
        } else if(!isSquare && !isRectangle) {
            binding!!.cropView.setAspectRatio(width, height)
        }else{
            binding!!.cropView.setAspectRatio(2, 1)
        }
        binding!!.cropView.setOnCropImageCompleteListener { _, result ->
            dismiss()
            onCropDoneListener?.onCropDone(result.uriContent)
        }
    }

    private fun onBackPress() {
        dismiss()
    }

    override fun onPause() {
        super.onPause()
        dismiss()
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

    interface OnCropDoneListener {
        fun onCropDone(uri: Uri?)
    }

    fun setCroppedImageSize(height: Int, width: Int) {
        croppedHeight = height
        croppedWidth = width
    }

    companion object {
        private const val ROTATE_DEGREES = 90
        private const val KEY_URI = "Key_Uri"
        private const val KEY_FROM_MEDIA_STORE = "Key_FromMedia"
        private const val KEY_SQUARE = "Key_SQUARE"
        private const val KEY_RECTANGLE = "Key_Rectangle"
        fun create(uri: Uri, isFromMediaStore: Boolean, isSquare: Boolean,isRectangle : Boolean): CropPhotoDialog {
            val bundle = Bundle()
            bundle.putParcelable(KEY_URI, uri)
            bundle.putBoolean(KEY_FROM_MEDIA_STORE, isFromMediaStore)
            bundle.putBoolean(KEY_SQUARE, isSquare)
            bundle.putBoolean(KEY_RECTANGLE, isRectangle)
            val dialog = CropPhotoDialog()
            dialog.arguments = bundle
            return dialog
        }
    }
}
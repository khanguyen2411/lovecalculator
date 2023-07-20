package com.hola360.crushlovecalculator.ui.profile

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.impl.utils.Exif
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.common.util.concurrent.ListenableFuture
import com.hola360.crushlovecalculator.NavMainGraphDirections
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseViewModelDialogFragment
import com.hola360.crushlovecalculator.base.basefragment.AbsBaseFragment
import com.hola360.crushlovecalculator.data.model.ProfileModel
import com.hola360.crushlovecalculator.databinding.LayoutTakePicktureBinding
import com.hola360.crushlovecalculator.dialog.crop.CropPhotoDialog
import com.hola360.crushlovecalculator.utils.SharedPreferenceUtils
import com.hola360.crushlovecalculator.utils.camerax.MainExecutor
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.Executor

class TakePicture: BaseViewModelDialogFragment<LayoutTakePicktureBinding>() {
    private  var setImageToCamera : SetImageToCamera? = null
    private var isTakePhoto = true

    private var changingCamera=false
    private var lensFacing = CameraSelector.DEFAULT_BACK_CAMERA

    fun setListener(listener : SetImageToCamera){
        setImageToCamera = listener
    }

    private val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> by lazy {
        ProcessCameraProvider.getInstance(requireContext())
    }
    private val imageExecutor: Executor by lazy {
        ContextCompat.getMainExecutor(requireContext())
    }
    private var cameraProvider:ProcessCameraProvider?=null
    private val imageCapture:ImageCapture by lazy {
        ImageCapture.Builder().build()
    }
    private var cameraFacingBack=true

    @SuppressLint("RestrictedApi")
    override fun initView() {
        binding!!.back.setOnClickListener {
            dismiss()
        }
        cameraProviderFuture.addListener({
            cameraProvider= cameraProviderFuture.get()
            val cameraSelector : CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            bindPreview(cameraProvider!!, cameraSelector)
        }, imageExecutor)
        binding!!.switchCamera.setOnClickListener{
            switchCamera()
        }
        binding!!.takePhoto.setOnClickListener {
            if(isTakePhoto){
                takePicture()
                isTakePhoto = false
            }

        }
    }

    private fun bindPreview(cameraProvider : ProcessCameraProvider, cameraSelector: CameraSelector) {
        cameraProvider.unbindAll()
        val preview : Preview = Preview.Builder()
            .build()
        preview.setSurfaceProvider(binding!!.previewView.surfaceProvider)
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
    }



    private fun switchCamera(){
        if(!changingCamera){
            changingCamera=true

            val cameraSelector= if(cameraFacingBack){
                CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build()
            }else{
                CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
            }
            cameraFacingBack= !cameraFacingBack
            bindPreview(cameraProvider!!, cameraSelector)
            Handler(Looper.getMainLooper()).postDelayed({
                changingCamera=false
            }, CHANGE_CAMERA_DELAY)
        }
    }

    protected val outputDirectory: String by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "${Environment.DIRECTORY_DCIM}/LovePhoto/"
        } else {
            "${requireContext().getExternalFilesDir(Environment.DIRECTORY_DCIM)}/LovePhoto/"
        }
    }


    private fun takePicture(){
        val localImageCapture = imageCapture
        val metadata = ImageCapture.Metadata().apply {
            isReversedHorizontal = lensFacing == CameraSelector.DEFAULT_FRONT_CAMERA
        }
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val outputOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis())
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            }
            val contentResolver = requireContext().contentResolver
            val contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

            ImageCapture.OutputFileOptions.Builder(contentResolver, contentUri, contentValues)
        } else {
            File(outputDirectory).mkdirs()
            val file = File(outputDirectory,"${System.currentTimeMillis()}.jpg")
            ImageCapture.OutputFileOptions.Builder(file)
        }.setMetadata(metadata).build()

        localImageCapture.takePicture(outputOptions, requireContext().mainExecutor(),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    outputFileResults.savedUri
                        ?.let { uri ->
                            setImageToCamera!!.setImage(uri)
                            dismiss()
                        }
                }
                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                }
            }
        )
    }

    fun Context.mainExecutor(): Executor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        mainExecutor
    } else {
        MainExecutor()
    }

    interface SetImageToCamera{
        fun setImage(uri : Uri)
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window!!.apply {
            setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            setDimAmount(1F)
        }
    }

    override fun getLayout(): Int {
        return R.layout.layout_take_pickture
    }
    companion object{
        private const val CHANGE_CAMERA_DELAY=1000L

        fun onCreate() : TakePicture{
            val takePicture = TakePicture()
            return takePicture
        }
    }

    override fun initViewModel() {
    }
}
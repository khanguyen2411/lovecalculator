package com.hola360.crushlovecalculator.ui.lovetest.resultcard

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.ViewDataBinding
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseLoveTestDialog
import com.hola360.crushlovecalculator.base.basedialog.OnDialogDismiss
import com.hola360.crushlovecalculator.data.Gallery
import com.hola360.crushlovecalculator.databinding.LayoutResultCardDialogBinding
import com.hola360.crushlovecalculator.ui.lovetest.LoveTestFragment
import com.hola360.crushlovecalculator.ui.lovetest.resultcard.dialogwidget.*
import com.hola360.crushlovecalculator.data.model.ProfileModel
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.PhotoUtils
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.snackbar.SnackBarType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultCardDialog: BaseLoveTestDialog<LayoutResultCardDialogBinding>() {

    private var view: BaseResultDialogWidget<out ViewDataBinding>?= null
    private var yourProfile: ProfileModel?=null
    private var crushProfile: ProfileModel?=null
    private var resultType:Int=0
    private var resultNumber:Int=0
    private var resultString: String?= null
    private var downloaded:Boolean=false
    private val actionCallback = object :ResultDialogCallback{
        override fun onDownload() {
            checkStoragePermission()
        }

        override fun onShare() {
            if(resultPicture != null){
                PhotoUtils.shareSinglePhoto(requireActivity(), resultPicture!!)
            }else{
                getBitmapFromView(true)
            }
        }
    }
    private var resultPicture: Gallery?= null
    private var takePicture=false
    private var callback:OnDialogDismiss?=null

    override fun initView() {
        binding!!.rootLayout.setOnClickListener {
            dismiss()
        }
        val viewTreeObserver= binding!!.contentLayout.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener {
            if(takePicture && !downloaded){
                downloaded=true
                Handler(Looper.getMainLooper()).postDelayed({
                    getBitmapFromView(false)
                }, 100)
            }
        }

    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun getBitmapFromView(share:Boolean){
        CoroutineScope(Dispatchers.IO).launch {
            val v= requireView().findViewById<LinearLayout>(R.id.loveCard)
            v?.let {
                val b = Bitmap.createBitmap(
                    v.width, v.height, Bitmap.Config.ARGB_8888
                )
                val c = Canvas(b)
                v.layout(v.left, v.top, v.right, v.bottom)
                v.draw(c)
                val gallery= PhotoUtils.savePhoto(requireContext(), Constants.PHOTO_PATH, b)
                withContext(Dispatchers.Main){
                    if(gallery != null){
                        resultPicture= gallery
                        if(share){
                            shareLoveCard()
                        }
                        setupWhenPictureTaken()
                    }else{
                        view!!.setDownloadImage(false)
                    }
                }
            }
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun setupWhenPictureTaken(){
        view!!.setDownloadImage(downloaded)
        showSuccessSnackBar()
    }

    private fun showSuccessSnackBar(){
        mainActivity.showSnackBar(SnackBarType.Success, getString(R.string.title_success), getString(R.string.love_test_result_download_success))
    }

    private fun shareLoveCard(){
        PhotoUtils.shareSinglePhoto(requireActivity(), resultPicture!!)
    }

    fun setupData(yourProfile: ProfileModel, crushProfile: ProfileModel, resultType:Int, resultNumber:Int, resultString:String){
        this.yourProfile= yourProfile
        this.crushProfile= crushProfile
        this.resultType= resultType
        this.resultNumber= resultNumber
        this.resultString= resultString
    }

    fun setDownloadImage(downloaded:Boolean){
        this.downloaded = downloaded
    }

    fun setDownloadImageWhenViewDrawn(){
        takePicture= true
    }

    private fun generateDialogContent():BaseResultDialogWidget<out ViewDataBinding>{
        return when(resultType){
            LoveTestFragment.PHOTO_TYPE->{
                PhotoContent(requireActivity(), actionCallback).apply {
                    onCreateDialog(yourProfile!!, crushProfile!!, resultNumber, resultString!!)
                }
            }
            LoveTestFragment.NAME_TYPE->{
                NameContent(requireActivity(), actionCallback).apply {
                    onCreateDialog(yourProfile!!, crushProfile!!, resultNumber, resultString!!)
                }
            }
            LoveTestFragment.FINGERPRINT_TYPE->{
                FingerprintContent(requireActivity(), actionCallback).apply {
                    onCreateDialog(yourProfile!!, crushProfile!!, resultNumber, resultString!!)
                }
            }
            LoveTestFragment.BIRTHDAY_TYPE->{
                BirthdayContent(requireActivity(), actionCallback).apply {
                    onCreateDialog(yourProfile!!, crushProfile!!, resultNumber, resultString!!)
                }
            }
            LoveTestFragment.NATIONALITY_TYPE->{
                NationalityContent(requireActivity(), actionCallback).apply {
                    onCreateDialog(yourProfile!!, crushProfile!!, resultNumber, resultString!!)
                }
            }
            LoveTestFragment.BMI_TYPE->{
                BmiContent(requireActivity(), actionCallback).apply {
                    onCreateDialog(yourProfile!!, crushProfile!!, resultNumber, resultString!!)
                }
            }
            else -> {
                JobContent(requireActivity(), actionCallback).apply {
                    onCreateDialog(yourProfile!!, crushProfile!!, resultNumber, resultString!!)
                }
            }
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onStart() {
        super.onStart()
        view= generateDialogContent()
        view!!.setDownloadImage(downloaded)
        binding!!.contentLayout.addView(view)
        dialog?.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)
    }

    override fun onStop() {
        super.onStop()
        binding!!.contentLayout.removeView(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback?.onDismiss()
    }

    private fun checkStoragePermission(){
        if(!SystemUtils.hasPermissions(requireContext(), *SystemUtils.getStoragePermissions())){
            storageResultLauncher.launch(SystemUtils.getStoragePermissions())
        }else{
            downloadPhoto()
        }
    }

    private val storageResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (SystemUtils.hasPermissions(requireContext(), *SystemUtils.getStoragePermissions())) {
            downloadPhoto()
        } else {
            SystemUtils.showAlertPermissionNotGrant(binding!!, requireActivity())
        }
    }

    private fun downloadPhoto(){
        downloaded=true
        getBitmapFromView(false)
    }

    interface ResultDialogCallback{
        fun onDownload()
        fun onShare()
    }

    companion object{
            fun create(callback: OnDialogDismiss): ResultCardDialog{
            val resultDialog= ResultCardDialog()
            resultDialog.callback= callback
            return resultDialog
        }
    }

    override fun getLayout(): Int {
        return R.layout.layout_result_card_dialog
    }

}
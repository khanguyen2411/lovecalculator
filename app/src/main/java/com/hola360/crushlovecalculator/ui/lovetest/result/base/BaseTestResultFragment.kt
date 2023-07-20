package com.hola360.crushlovecalculator.ui.lovetest.result.base

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.OnDialogDismiss
import com.hola360.crushlovecalculator.base.basefragment.AbsBaseFragment
import com.hola360.crushlovecalculator.ui.lovetest.LoveTestFragment
import com.hola360.crushlovecalculator.ui.lovetest.resultcard.ResultCardDialog
import com.hola360.crushlovecalculator.data.model.ProfileModel
import com.hola360.crushlovecalculator.utils.SystemUtils

abstract class BaseTestResultFragment<V:ViewDataBinding?>: AbsBaseFragment<V>(), View.OnClickListener  {

    protected var yourProfile: ProfileModel?=null
    protected var crushProfile: ProfileModel?=null
    protected var resultNumber:Int=0
    protected var resultString:String?=null
    private var showDialog=false
    protected var actionDownload=true

    protected val resultDialog: ResultCardDialog by lazy {
        ResultCardDialog.create(object : OnDialogDismiss {
            override fun onDismiss() {
                showDialog=false
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupData()
    }

    abstract fun setupData()

    protected fun generateName(profileModel: ProfileModel, isYou:Boolean):String{
        return  if(profileModel.name.isNullOrEmpty()){
            if(isYou){
                mainActivity.getString(R.string.love_test_result_you)
            }else{
                mainActivity.getString(R.string.love_test_result_crush)
            }
        }else{
            profileModel.name!!
        }
    }

    protected fun checkStoragePermission(){
        if(!SystemUtils.hasPermissions(requireContext(), *SystemUtils.getStoragePermissions())){
            storageResultLauncher.launch(SystemUtils.getStoragePermissions())
        }else{
            if(actionDownload){
                openResultDialogAndDownload()
            }else{
                shareResult()
            }
        }
    }

    private val storageResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (SystemUtils.hasPermissions(requireContext(), *SystemUtils.getStoragePermissions())) {
            if(actionDownload){
                openResultDialogAndDownload()
            }else{
                shareResult()
            }
        } else {
            SystemUtils.showAlertPermissionNotGrant(binding!!, requireActivity())
        }
    }

    protected fun openResultDialogAndDownload(){
        if(!showDialog){
            showDialog=true
            resultDialog.setDownloadImageWhenViewDrawn()
            resultDialog.show(requireActivity().supportFragmentManager, "result_card")
        }
    }

    protected fun shareResult(){
        if(!showDialog){
            showDialog=true
            resultDialog.show(requireActivity().supportFragmentManager, "result_card")
        }
    }

    override fun onPause() {
        super.onPause()
        resultDialog.dialog?.dismiss()
    }

    protected fun testAgain(testType:Int){
        findNavController().popBackStack(R.id.nav_love_test, false)
        when(testType){
            LoveTestFragment.PHOTO_TYPE->{
                findNavController().navigate(R.id.nav_test_photo)
            }
            LoveTestFragment.NAME_TYPE->{
                findNavController().navigate(R.id.nav_test_name)
            }
            LoveTestFragment.FINGERPRINT_TYPE->{
                findNavController().navigate(R.id.nav_test_fingerprint)
            }
            LoveTestFragment.BIRTHDAY_TYPE->{
                findNavController().navigate(R.id.nav_test_birthday)
            }
            LoveTestFragment.NATIONALITY_TYPE->{
                findNavController().navigate(R.id.nav_test_nationality)
            }
            LoveTestFragment.BMI_TYPE->{
                findNavController().navigate(R.id.nav_test_bmi)
            }
            else->{
                findNavController().navigate(R.id.nav_test_job)
            }
        }
    }

}
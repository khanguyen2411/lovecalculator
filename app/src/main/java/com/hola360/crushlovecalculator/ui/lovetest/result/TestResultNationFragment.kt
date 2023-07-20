package com.hola360.crushlovecalculator.ui.lovetest.result

import android.view.View
import android.view.ViewOutlineProvider
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.databinding.LayoutTestResultNationalityBinding
import com.hola360.crushlovecalculator.ui.lovetest.LoveTestFragment
import com.hola360.crushlovecalculator.ui.lovetest.result.base.BaseTestResultFragment

class TestResultNationFragment: BaseTestResultFragment<LayoutTestResultNationalityBinding>(){

    private val args:TestResultPhotoFragmentArgs by lazy {
        TestResultPhotoFragmentArgs.fromBundle(requireArguments())
    }

    override fun setupData() {
        yourProfile = args.yourProfile
        crushProfile= args.crushProfile
        resultNumber= args.testResult
        resultString= args.stringResult
        resultDialog.setupData(args.yourProfile, args.crushProfile, args.testType, args.testResult, args.stringResult)
    }

    override fun initView() {
        binding!!.toolbar.back.setOnClickListener(this)
        binding!!.toolbar.toolbarTitle.text= mainActivity.getString(R.string.love_test_result_title)
        binding!!.toolbar.download.setOnClickListener(this)
        binding!!.action.testAgain.setOnClickListener(this)
        binding!!.action.share.setOnClickListener(this)
        binding!!.action.resultNumber.text= resultNumber.toString().plus("%")
        binding!!.action.stringResult.text= resultString
        binding!!.yourProfile= yourProfile
        binding!!.crushProfile= crushProfile
        binding!!.yourName.text= generateName(yourProfile!!, true)
        binding!!.crushName.text= generateName(crushProfile!!, false)
        binding!!.yourNationFlag.outlineProvider= ViewOutlineProvider.BACKGROUND
        binding!!.yourNationFlag.clipToOutline= true
        binding!!.crushNationFlag.outlineProvider= ViewOutlineProvider.BACKGROUND
        binding!!.crushNationFlag.clipToOutline= true
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.back->{
                findNavController().popBackStack()
            }
            R.id.testAgain->{
                testAgain(LoveTestFragment.NATIONALITY_TYPE)
            }
            R.id.download->{
                actionDownload=true
                checkStoragePermission()
            }
            R.id.share->{
                actionDownload=false
                checkStoragePermission()
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.layout_test_result_nationality
    }
}
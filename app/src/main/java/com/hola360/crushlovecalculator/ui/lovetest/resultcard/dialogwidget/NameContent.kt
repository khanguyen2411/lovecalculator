package com.hola360.crushlovecalculator.ui.lovetest.resultcard.dialogwidget

import android.annotation.SuppressLint
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.databinding.LayoutTestResultDialogNameBinding
import com.hola360.crushlovecalculator.ui.lovetest.resultcard.ResultCardDialog
import com.hola360.crushlovecalculator.utils.DataBindingUtils.setHeartPercent

@SuppressLint("ViewConstructor")
class NameContent(mainActivity: FragmentActivity, val callback: ResultCardDialog.ResultDialogCallback): BaseResultDialogWidget<LayoutTestResultDialogNameBinding>(mainActivity), View.OnClickListener {

    override val layoutId: Int
        get() = R.layout.layout_test_result_dialog_name

    override fun initView() {
        binding!!.yourProfile= yourProfile
        binding!!.crushProfile= crushProfile
        binding!!.resultString.text= resultString
        binding!!.resultNumber.text= resultNumber.toString().plus("%")
        binding!!.yourName.text= generateName(yourProfile!!, true)
        binding!!.crushName.text= generateName(crushProfile!!, false)
        binding!!.dialogBottom.download.setOnClickListener(this)
        binding!!.dialogBottom.share.setOnClickListener(this)
        binding!!.heartPercent.heartOne.setHeartPercent(0, resultNumber)
        binding!!.heartPercent.heartTwo.setHeartPercent(1, resultNumber)
        binding!!.heartPercent.heartThree.setHeartPercent(2, resultNumber)
        binding!!.heartPercent.heartFour.setHeartPercent(3, resultNumber)
        binding!!.heartPercent.heartFive.setHeartPercent(4, resultNumber)
    }

    override fun setDownloadImage(downloaded: Boolean) {
        binding!!.dialogBottom.download.visibility= if(downloaded){
            View.GONE
        }else{
            binding!!.dialogBottom.download.isClickable= true
            View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.download->{
                binding!!.dialogBottom.download.isClickable=false
                callback.onDownload()
            }
            R.id.share->{
                callback.onShare()
            }
        }
    }


}
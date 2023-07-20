package com.hola360.crushlovecalculator.ui.lovetest.resultcard.dialogwidget

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.model.ProfileModel

abstract class BaseResultDialogWidget<V: ViewDataBinding?>(val mainActivity: FragmentActivity): LinearLayout(mainActivity) {

    protected var binding: V?=null
    protected abstract val layoutId:Int
    protected var yourProfile: ProfileModel?=null
    protected var crushProfile: ProfileModel?=null
    protected var resultNumber:Int=0
    protected var resultString:String?=null
    protected var downloaded:Boolean=false

    fun onCreateDialog(yourProfile: ProfileModel, crushProfile: ProfileModel, resultNumber:Int, resultString:String){
        this.yourProfile= yourProfile
        this.crushProfile= crushProfile
        this.resultNumber= resultNumber
        this.resultString= resultString
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, this, true)
        initView()
    }

    abstract  fun setDownloadImage(downloaded:Boolean)

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

    abstract fun initView()
}
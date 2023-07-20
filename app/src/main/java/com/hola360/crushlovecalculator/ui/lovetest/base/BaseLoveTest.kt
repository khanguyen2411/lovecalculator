package com.hola360.crushlovecalculator.ui.lovetest.base

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.NavMainGraphDirections
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basefragment.AbsBaseFragment
import com.hola360.crushlovecalculator.data.api.imagestore.RetrofitApi
import com.hola360.crushlovecalculator.data.api.imagestore.RetrofitHelper
import com.hola360.crushlovecalculator.data.model.ProfileModel
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.snackbar.SnackBarType

abstract class BaseLoveTest<V:ViewDataBinding>: AbsBaseFragment<V>() {

    protected val yourProfileModel: ProfileModel by lazy {
        dataPref.getUserProfileModel(false)
    }
    protected val crushProfileModel by lazy {
        ProfileModel()
    }
    protected var youPick=true
    protected var showDialog=false
    protected val edtYourNameChangeListener= object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            yourProfileModel.name= s?.trim().toString()
        }
    }
    protected val edtCrushNameChangeListener= object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            crushProfileModel.name= s?.trim().toString()
        }
    }
    private val retrofitApi: RetrofitApi by lazy {
        RetrofitHelper.getInstance().storeApi
    }
    protected var testResult:Int=0
    protected var resultString:String?=null
    protected var getTestStringResult=false

    protected fun backButton(){
        if(!showDialog){
            findNavController().popBackStack()
            SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
        }
    }
    abstract fun updateYourProfile()
    abstract fun updateCrushProfile()

    protected fun focusEdittext(editText: EditText){
        editText.requestFocus()
        SystemUtils.showSoftKeyboard(requireContext(), editText)
    }

    protected fun navigateToAnimation(testType:Int){
        findNavController().navigate(
            NavMainGraphDirections.actionToNavTestResultAnimation(
                yourProfileModel,
                crushProfileModel,
                testType,
                testResult
            )
        )
    }

    protected fun showNetworkWarning() {
        mainActivity.showSnackBar(SnackBarType.Error,
            getString(R.string.love_test_network_error_title),
            getString(R.string.love_test_network_error_msg)
        )
    }

    companion object{
        const val ENGLAND_CODE= "gb"
    }
}
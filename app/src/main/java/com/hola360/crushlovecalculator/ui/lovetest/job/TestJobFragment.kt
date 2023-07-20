package com.hola360.crushlovecalculator.ui.lovetest.job

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewOutlineProvider
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.databinding.FragmentTestJobBinding
import com.hola360.crushlovecalculator.dialog.jobdialog.JobDialog
import com.hola360.crushlovecalculator.dialog.nationdialog.NationDialog
import com.hola360.crushlovecalculator.ui.lovetest.LoveTestFragment
import com.hola360.crushlovecalculator.ui.lovetest.base.BaseLoveTest
import com.hola360.crushlovecalculator.ui.lovetest.photo.TestPhotoFragment
import com.hola360.crushlovecalculator.utils.LoveTestUtils
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.snackbar.SnackBarType
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class TestJobFragment: BaseLoveTest<FragmentTestJobBinding>(), View.OnClickListener {

    private val jobList: MutableList<String> by lazy {
        JobDialog.getListJob(requireContext())
    }
    private val jobDialog: JobDialog by lazy {
        JobDialog.create(object : NationDialog.NationDialogCallback{
            override fun onSelectItem(job: String) {
                if(youPick){
                    if(job.trim().lowercase() != "other"){
                        yourProfileModel.job= job
                        updateYourProfile()
                    }else{
                        binding!!.edtYourJob.requestFocus()
                        binding!!.edtYourJob.text?.length?.let { binding!!.edtYourJob.setSelection(it) }
                        SystemUtils.showSoftKeyboard(requireContext(), binding!!.root)
                    }
                }else{
                    if(job.trim().lowercase() != "other"){
                        crushProfileModel.job= job
                        updateCrushProfile()
                    }else{
                        binding!!.edtCrushJob.requestFocus()
                        binding!!.edtCrushJob.text?.length?.let { binding!!.edtCrushJob.setSelection(it) }
                        SystemUtils.showSoftKeyboard(requireContext(), binding!!.root)
                    }
                }
            }
            override fun onDismiss() {
                showDialog=false
            }
        })
    }

    override fun initView() {
        getTestStringResult=false
        binding!!.toolbar.back.setOnClickListener(this)
        binding!!.toolbar.toolbarTitle.text = getString(R.string.love_test_job_title)
        binding!!.txtSelectYourJob.setOnClickListener(this)
        binding!!.txtSelectCrushJob.setOnClickListener(this)
        binding!!.edtYourName.addTextChangedListener(edtYourNameChangeListener)
        binding!!.edtCrushName.addTextChangedListener(edtCrushNameChangeListener)
        binding!!.edtYourJob.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    yourProfileModel.job=if(s.trim().isEmpty()){
                        null
                    }else{
                        binding!!.yourEmpty=false
                        val job= s.trim().substring(0,1).uppercase() + s.trim().substring(1)
                        job
                    }
                }
            }
        })
        binding!!.edtCrushJob.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    crushProfileModel.job=if(s.trim().isEmpty()){
                        null
                    }else{
                        binding!!.crushEmpty=false
                        val job= s.trim().substring(0,1).uppercase() + s.trim().substring(1)
                        job
                    }
                }
            }
        })
        binding!!.startTest.setOnClickListener(this)
        jobDialog.setDialogCurOwnerId(R.id.nav_test_job)
        setupProfile()
        KeyboardVisibilityEvent.setEventListener(mainActivity, this) {
            if (it) {
                binding!!.appBarLayout.setExpanded(false)
            } else {
                binding!!.appBarLayout.setExpanded(true)
            }
        }
    }

    override fun updateYourProfile() {
        binding!!.yourProfile= yourProfileModel
    }

    override fun updateCrushProfile() {
        binding!!.crushProfile= crushProfileModel
    }

    private fun setupProfile(){
        binding!!.edtYourName.setText(yourProfileModel.name)
        binding!!.yourProfile= yourProfileModel
        binding!!.crushProfile= crushProfileModel
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.back->{
                findNavController().popBackStack()
                SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
            }
            R.id.txtSelectYourJob->{
                openJobDialog(isYou = true)
            }
            R.id.txtSelectCrushJob->{
                openJobDialog(isYou = false)
            }
            R.id.startTest->{
                checkBeforeStartTest()
            }

        }
    }

    private fun openJobDialog(isYou:Boolean){
        if(!showDialog){
            showDialog=true
            youPick= isYou
            val pos= if(isYou){
                jobList.indexOf(yourProfileModel.job?.lowercase()).coerceAtLeast(0)
            }else{
                jobList.indexOf(crushProfileModel.job?.lowercase()).coerceAtLeast(0)
            }
            jobDialog.setSelectPosition(pos)
            jobDialog.show(requireActivity().supportFragmentManager, "job_dialog")
        }
    }

    private fun checkBeforeStartTest(){
        if(binding!!.edtYourJob.text.trim().isEmpty()){
            binding!!.yourEmpty=true
            focusEdittext(binding!!.edtYourJob)
            return
        }
        if(binding!!.edtCrushJob.text.trim().isEmpty()){
            binding!!.crushEmpty=true
            focusEdittext(binding!!.edtCrushJob)
            return
        }
        if(SystemUtils.checkConnection(requireContext())){
            testResult= LoveTestUtils.getNameMatch(yourProfileModel.job!!, crushProfileModel.job!!)
            SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
            navigateToAnimation(LoveTestFragment.JOB_TYPE)
        }else{
            showNetworkWarning()
        }
    }
    override fun getLayout(): Int {
        return R.layout.fragment_test_job
    }

}
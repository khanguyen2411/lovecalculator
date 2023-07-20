package com.hola360.crushlovecalculator.ui.lovetest.nationality

import android.view.View
import android.view.ViewOutlineProvider
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.databinding.FragmentTestNationalityBinding
import com.hola360.crushlovecalculator.dialog.nationdialog.NationDialog
import com.hola360.crushlovecalculator.dialog.nationdialog.NationModel
import com.hola360.crushlovecalculator.dialog.nationdialog.NationUtils
import com.hola360.crushlovecalculator.ui.lovetest.LoveTestFragment
import com.hola360.crushlovecalculator.ui.lovetest.base.BaseLoveTest
import com.hola360.crushlovecalculator.utils.LoveTestUtils
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.snackbar.SnackBarType
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class TestNationalityFragment: BaseLoveTest<FragmentTestNationalityBinding>(), View.OnClickListener {

    private val nations:MutableList<NationModel> by lazy {
        NationUtils.generateNationList(requireContext())
    }
    private val nationDialog: NationDialog by lazy {
        NationDialog.create(object : NationDialog.NationDialogCallback{
            override fun onSelectItem(nationCode: String) {
                val nation= nations.find { it.nationInformation?.code!! == nationCode }
                if(youPick){
                    yourProfileModel.nation= nation
                    updateYourProfile()
                }else{
                    crushProfileModel.nation= nation
                    updateCrushProfile()
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
        binding!!.toolbar.toolbarTitle.text= getString(R.string.love_test_nation_title)
        binding!!.yourNation.setOnClickListener(this)
        binding!!.selectYourNation.setOnClickListener(this)
        binding!!.crushNation.setOnClickListener(this)
        binding!!.selectCrushNation.setOnClickListener(this)
        binding!!.startTest.setOnClickListener(this)
        binding!!.edtYourName.addTextChangedListener(edtYourNameChangeListener)
        binding!!.edtCrushName.addTextChangedListener(edtCrushNameChangeListener)
        binding!!.yourNationFlag.outlineProvider= ViewOutlineProvider.BACKGROUND
        binding!!.yourNationFlag.clipToOutline= true
        binding!!.crushNationFlag.outlineProvider= ViewOutlineProvider.BACKGROUND
        binding!!.crushNationFlag.clipToOutline= true
        nationDialog.setDialogCurOwnerId(R.id.nav_test_nationality)
        setupNation()
        KeyboardVisibilityEvent.setEventListener(mainActivity, this) {
            if (it) {
                binding!!.appBarLayout.setExpanded(false)
            } else {
                binding!!.appBarLayout.setExpanded(true)
            }
        }
    }

    private fun setupNation(){
        binding!!.edtYourName.setText(yourProfileModel.name)
        if(yourProfileModel.nation== null){
            yourProfileModel.nation= nations.find { it.nationInformation!!.code!!.lowercase().contains("gb") }
        }
        if(crushProfileModel.nation ==null){
            crushProfileModel.nation= nations.find { it.nationInformation!!.code!!.lowercase().contains("gb") }
        }
        updateYourProfile()
        updateCrushProfile()
    }

    override fun updateYourProfile() {
        binding!!.yourProfile= yourProfileModel
    }

    override fun updateCrushProfile() {
        binding!!.crushProfile= crushProfileModel
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.back->{
                findNavController().popBackStack()
                SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
            }
            R.id.yourNation, R.id.selectYourNation->{
                openNationFlag(true)
            }
            R.id.crushNation, R.id.selectCrushNation->{
                openNationFlag(false)
            }
            R.id.startTest->{
                startTestNation()
            }

        }
    }

    private fun openNationFlag(isYou:Boolean){
        if(!showDialog){
            showDialog=true
            youPick=isYou
            SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
            val nationName= if(youPick){
                yourProfileModel.nation?.nationInformation?.name
            }else{
                crushProfileModel.nation?.nationInformation?.name
            }
            val nationPos= nations.indexOfFirst { it.nationInformation!!.name.equals(nationName) }
            if(nationPos != -1){
                nationDialog.setSelectPosition(nationPos)
            }
            nationDialog.show(requireActivity().supportFragmentManager, "nation_dialog")
        }
    }

    private fun startTestNation(){
        if(SystemUtils.checkConnection(requireContext())){
            testResult= if(yourProfileModel.nation == crushProfileModel.nation){
                LoveTestUtils.MAX_LOVE_MATCH
            }else{
                LoveTestUtils.getNameMatch(yourProfileModel.nation!!.nationInformation!!.name!! ,crushProfileModel.nation!!.nationInformation!!.name!!)
            }
            SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
            navigateToAnimation(LoveTestFragment.NATIONALITY_TYPE)
        }else{
            showNetworkWarning()
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_test_nationality
    }


}
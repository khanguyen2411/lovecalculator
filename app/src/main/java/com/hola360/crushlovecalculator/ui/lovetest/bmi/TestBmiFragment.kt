package com.hola360.crushlovecalculator.ui.lovetest.bmi

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewOutlineProvider
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.databinding.FragmentTestBmiBinding
import com.hola360.crushlovecalculator.dialog.unitpopup.ItemUnitAdapter
import com.hola360.crushlovecalculator.dialog.unitpopup.UnitPopup
import com.hola360.crushlovecalculator.ui.lovetest.LoveTestFragment
import com.hola360.crushlovecalculator.ui.lovetest.base.BaseLoveTest
import com.hola360.crushlovecalculator.ui.lovetest.photo.TestPhotoFragment
import com.hola360.crushlovecalculator.utils.LoveTestUtils
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.Utils
import com.hola360.crushlovecalculator.utils.snackbar.SnackBarType
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class TestBmiFragment : BaseLoveTest<FragmentTestBmiBinding>(), View.OnClickListener {

    private val heightUnits: MutableList<String> by lazy {
        resources.getStringArray(R.array.height_unit).toMutableList()
    }
    private val weightUnits: MutableList<String> by lazy {
        resources.getStringArray(R.array.weight_unit).toMutableList()
    }
    private val unitPopupDialog: UnitPopup by lazy {
        UnitPopup(requireContext())
    }

    override fun initView() {
        getTestStringResult = false
        binding!!.yourProfile = yourProfileModel
        binding!!.crushProfile = crushProfileModel
        binding!!.toolbar.back.setOnClickListener(this)
        binding!!.toolbar.toolbarTitle.text = getString(R.string.love_test_bmi_title)
        binding!!.txtYourHeightUnit.setOnClickListener(this)
        binding!!.txtYourWeightUnit.setOnClickListener(this)
        binding!!.txtCrushHeightUnit.setOnClickListener(this)
        binding!!.txtCrushWeightUnit.setOnClickListener(this)
        binding!!.startTest.setOnClickListener(this)
        binding!!.edtYourHeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                yourProfileModel.height = if (s.toString().toFloatOrNull() == null) {
                    null
                } else {
                    Utils.roundDecimalNumber(s!!.trim().toString())
                }
                if (yourProfileModel.height != null && yourProfileModel.weight != null) {
                    binding!!.yourEmpty = false
                }
            }
        })
        binding!!.edtYourWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                yourProfileModel.weight = if (s.toString().toFloatOrNull() == null) {
                    null
                } else {
                    Utils.roundDecimalNumber(s!!.trim().toString())
                }
                if (yourProfileModel.height != null && yourProfileModel.weight != null) {
                    binding!!.yourEmpty = false
                }
            }
        })
        binding!!.edtCrushHeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                crushProfileModel.height = if (s.toString().toFloatOrNull() == null) {
                    null
                } else {
                    Utils.roundDecimalNumber(s!!.trim().toString())
                }
                if (crushProfileModel.height != null && crushProfileModel.weight != null) {
                    binding!!.crushEmpty = false
                }
            }
        })
        binding!!.edtCrushWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                crushProfileModel.weight = if (s.toString().toFloatOrNull() == null) {
                    null
                } else {
                    Utils.roundDecimalNumber(s!!.trim().toString())
                }
                if (crushProfileModel.height != null && crushProfileModel.weight != null) {
                    binding!!.crushEmpty = false
                }
            }
        })
        binding!!.edtYourName.addTextChangedListener(edtYourNameChangeListener)
        binding!!.edtCrushName.addTextChangedListener(edtCrushNameChangeListener)
        binding!!.edtYourName.setText(yourProfileModel.name)
        KeyboardVisibilityEvent.setEventListener(mainActivity, this) {
            if (it) {
                binding!!.appBarLayout.setExpanded(false)
            } else {
                binding!!.appBarLayout.setExpanded(true)
            }
        }
    }

    override fun updateCrushProfile() {
        binding!!.crushProfile = crushProfileModel
    }

    override fun updateYourProfile() {
        binding!!.yourProfile = yourProfileModel
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back -> {
                findNavController().popBackStack()
                SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
            }
            R.id.txtYourHeightUnit -> {
                openUnitDialog(isHeight = true, isYou = true, view = binding!!.txtYourHeightUnit)
            }
            R.id.txtYourWeightUnit -> {
                openUnitDialog(isHeight = false, isYou = true, view = binding!!.txtYourHeightUnit)
            }
            R.id.txtCrushHeightUnit -> {
                openUnitDialog(isHeight = true, isYou = false, view = binding!!.txtCrushHeightUnit)
            }
            R.id.txtCrushWeightUnit -> {
                openUnitDialog(isHeight = false, isYou = false, view = binding!!.txtCrushWeightUnit)
            }
            R.id.startTest -> {
                checkBeforeStartTest()
            }
        }
    }

    private fun openUnitDialog(isHeight: Boolean, isYou: Boolean, view: View) {
        val unit = if (isHeight) {
            heightUnits
        } else {
            weightUnits
        }
        youPick = isYou
        unitPopupDialog.updateData(unit, object : ItemUnitAdapter.OnUnitItemClick {
            override fun onUnitClick(position: Int) {
                if (isYou) {
                    if (isHeight) {
                        yourProfileModel.heightUnitCm = position == 0
                    } else {
                        yourProfileModel.weightUnitKg = position == 0
                    }
                    updateYourProfile()
                } else {
                    if (isHeight) {
                        crushProfileModel.heightUnitCm = position == 0
                    } else {
                        crushProfileModel.weightUnitKg = position == 0
                    }
                    updateCrushProfile()
                }
                unitPopupDialog.close()
            }
        })
        unitPopupDialog.show(binding!!.root.bottom, view)
    }

    private fun checkBeforeStartTest() {
        if (yourProfileModel.height == null || yourProfileModel.weight == null || yourProfileModel.height == 0f || yourProfileModel.weight == 0f) {
            binding!!.yourEmpty = true
            binding!!.scroll.smoothScrollTo(0, binding!!.yourBmiLayout.top)
            if (binding!!.edtYourHeight.text.trim().isEmpty()) {
                focusEdittext(binding!!.edtYourHeight)
                return
            }
            if (binding!!.edtYourWeight.text.trim().isEmpty()) {
                focusEdittext(binding!!.edtYourWeight)
                return
            }
            return
        }
        if (crushProfileModel.height == null || crushProfileModel.weight == null || crushProfileModel.height == 0f || crushProfileModel.weight == 0f) {
            binding!!.crushEmpty = true
            if (binding!!.edtCrushHeight.text.trim().isEmpty()) {
                focusEdittext(binding!!.edtCrushHeight)
                return
            }
            if (binding!!.edtCrushWeight.text.trim().isEmpty()) {
                focusEdittext(binding!!.edtCrushWeight)
                return
            }
            return
        }
        if (SystemUtils.checkConnection(requireContext())) {
            testResult = LoveTestUtils.getBMIMatch(
                yourProfileModel.height!!,
                yourProfileModel.heightUnitCm,
                yourProfileModel.weight!!,
                yourProfileModel.weightUnitKg,
                crushProfileModel.height!!,
                crushProfileModel.heightUnitCm,
                crushProfileModel.weight!!,
                crushProfileModel.weightUnitKg
            )
            SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
            navigateToAnimation(LoveTestFragment.BMI_TYPE)
        } else {
            mainActivity.showSnackBar(SnackBarType.Error,
                getString(R.string.love_test_network_error_title),
                getString(R.string.love_test_network_error_msg)
            )
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_test_bmi
    }
    override fun onDestroy() {
        super.onDestroy()
        unitPopupDialog.close()
    }
}
package com.hola360.crushlovecalculator.ui.lovetest.birthday

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.OnDialogDismiss
import com.hola360.crushlovecalculator.databinding.FragmentTestBirthdayBinding
import com.hola360.crushlovecalculator.dialog.lovetimedialog.DatePickerDiaLog
import com.hola360.crushlovecalculator.ui.lovetest.LoveTestFragment
import com.hola360.crushlovecalculator.ui.lovetest.base.BaseLoveTest
import com.hola360.crushlovecalculator.utils.LoveTestUtils
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.snackbar.SnackBarType
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class TestBirthdayFragment : BaseLoveTest<FragmentTestBirthdayBinding>(), View.OnClickListener {
    override fun initView() {
        getTestStringResult = false
        binding!!.toolbar.back.setOnClickListener(this)
        binding!!.toolbar.toolbarTitle.text = getString(R.string.love_test_birthday_title)
        binding!!.yourEmpty = false
        binding!!.crushEmpty = false
        binding!!.yourProfile = yourProfileModel
        binding!!.crushProfile = crushProfileModel
        binding!!.edtYourName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                yourProfileModel.name = s?.trim()?.toString()
            }
        })
        binding!!.edtCrushName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                crushProfileModel.name = s?.trim()?.toString()
            }
        })
        binding!!.pickYourDate.setOnClickListener(this)
        binding!!.txtYourBirthday.setOnClickListener(this)
        binding!!.pickCrushDate.setOnClickListener(this)
        binding!!.txtCrushBirthday.setOnClickListener(this)
        binding!!.startTest.setOnClickListener(this)
        binding!!.edtYourName.setText(yourProfileModel.name)
        KeyboardVisibilityEvent.setEventListener(mainActivity, this) {
            if (it) {
                binding!!.appBarLayout.setExpanded(false)
            } else {
                binding!!.appBarLayout.setExpanded(true)
            }
        }
    }

    override fun updateYourProfile() {
        binding!!.yourProfile = yourProfileModel
    }

    override fun updateCrushProfile() {
        binding!!.crushProfile = crushProfileModel
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back -> {
                findNavController().popBackStack()
                SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
            }
            R.id.pickYourDate, R.id.txtYourBirthday -> {
                pickBirthday(true)
            }
            R.id.pickCrushDate, R.id.txtCrushBirthday -> {
                pickBirthday(false)
            }
            R.id.startTest -> {
                checkBeforeStartTest()
            }

        }
    }

    private fun pickBirthday(yourPick: Boolean) {
        val datePickerDiaLog = DatePickerDiaLog.create(
            if (yourPick) {
                getString(R.string.love_test_birthday_your_date)
            } else {
                getString(R.string.love_test_birthday_crush_date)
            }, true
        )
        val time = if (yourPick) {
            yourProfileModel.dateOfBirth ?: System.currentTimeMillis()
        } else {
            crushProfileModel.dateOfBirth ?: System.currentTimeMillis()
        }
        datePickerDiaLog.setCurDate(time)
        datePickerDiaLog.onDatePickerListener = object : DatePickerDiaLog.OnDatePickerListener {
            override fun onDatePicked(time: Long) {
                if (yourPick) {
                    yourProfileModel.dateOfBirth = time
                    updateYourProfile()
                    binding!!.yourEmpty = false
                } else {
                    crushProfileModel.dateOfBirth = time
                    updateCrushProfile()
                    binding!!.crushEmpty = false
                }
            }

            override fun onDismiss() {

            }
        }
        datePickerDiaLog.show(requireActivity().supportFragmentManager, "birthday_dialog")
    }

    private fun checkBeforeStartTest() {
        if (yourProfileModel.dateOfBirth == null) {
            binding!!.yourEmpty = true
            return
        }
        if (crushProfileModel.dateOfBirth == null) {
            binding!!.crushEmpty = true
            return
        }
        if (SystemUtils.checkConnection(requireContext())) {
            testResult = LoveTestUtils.getBirthdayMatch(
                yourProfileModel.dateOfBirth!!,
                crushProfileModel.dateOfBirth!!
            )
            SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
            navigateToAnimation(LoveTestFragment.BIRTHDAY_TYPE)
        } else {
            mainActivity.showSnackBar(
                SnackBarType.Error,
                getString(R.string.love_test_network_error_title),
                getString(R.string.love_test_network_error_msg)
            )
        }
    }


    override fun getLayout(): Int {
        return R.layout.fragment_test_birthday
    }
}
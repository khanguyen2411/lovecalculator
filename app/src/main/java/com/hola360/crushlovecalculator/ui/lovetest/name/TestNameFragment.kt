package com.hola360.crushlovecalculator.ui.lovetest.name

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewOutlineProvider
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.databinding.FragmentTestNameBinding
import com.hola360.crushlovecalculator.ui.lovetest.LoveTestFragment
import com.hola360.crushlovecalculator.ui.lovetest.base.BaseLoveTest
import com.hola360.crushlovecalculator.ui.lovetest.photo.TestPhotoFragment
import com.hola360.crushlovecalculator.utils.LoveTestUtils
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.snackbar.SnackBarType
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener


class TestNameFragment : BaseLoveTest<FragmentTestNameBinding>(), View.OnClickListener {
    override fun initView() {
        getTestStringResult = false
        binding!!.toolbar.back.setOnClickListener(this)
        binding!!.toolbar.toolbarTitle.text = getString(R.string.love_test_name_title)
        binding!!.yourEmpty = false
        binding!!.crushEmpty = false
        binding!!.edtYourName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                yourProfileModel.name = s?.trim().toString()
                if (!s.isNullOrEmpty()) {
                    binding!!.yourEmpty = false
                }
            }
        })
        binding!!.edtCrushName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                crushProfileModel.name = s?.trim().toString()
                if (!s.isNullOrEmpty()) {
                    binding!!.crushEmpty = false
                }
            }
        })
        binding!!.startTest.setOnClickListener(this)
        if (!yourProfileModel.name.isNullOrEmpty()) {
            binding!!.edtYourName.setText(yourProfileModel.name)
        }
        setEventListener(mainActivity, this) {
            if (it) {
                binding!!.appBarLayout.setExpanded(false)
            } else {
                binding!!.appBarLayout.setExpanded(true)
            }
        }
    }

    override fun updateYourProfile() {

    }

    override fun updateCrushProfile() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back -> {
                findNavController().popBackStack()
                SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
            }
            R.id.startTest -> {
                checkBeforeStartTest()
            }

        }
    }

    private fun checkBeforeStartTest() {
        if (binding!!.edtYourName.text.trim().isEmpty()) {
            binding!!.yourEmpty = true
            focusEdittext(binding!!.edtYourName)
            return
        }
        if (binding!!.edtCrushName.text.trim().isEmpty()) {
            binding!!.crushEmpty = true
            focusEdittext(binding!!.edtCrushName)
            return
        }
        if (SystemUtils.checkConnection(requireContext())) {
            val yourName = binding!!.edtYourName.text.trim().toString()
            val crushName = binding!!.edtCrushName.text.trim().toString()
            testResult = LoveTestUtils.getNameMatch(yourName, crushName)
            SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
            navigateToAnimation(LoveTestFragment.NAME_TYPE)
        } else {
            showNetworkWarning()
        }
    }
    override fun getLayout(): Int {
        return R.layout.fragment_test_name
    }

}
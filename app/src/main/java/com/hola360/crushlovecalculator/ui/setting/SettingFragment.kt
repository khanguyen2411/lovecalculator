package com.hola360.crushlovecalculator.ui.setting
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basefragment.AbsBaseFragment
import com.hola360.crushlovecalculator.databinding.FragmentSettingBinding


class SettingFragment: AbsBaseFragment<FragmentSettingBinding>() {
    override fun initView() {
        binding!!.toolbar.setNavigationOnClickListener { onBackPress() }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_setting
    }

    private fun onBackPress() {
        findNavController().navigateUp()
    }

}
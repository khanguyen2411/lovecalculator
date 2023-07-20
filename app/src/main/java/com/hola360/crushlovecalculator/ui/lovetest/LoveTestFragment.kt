package com.hola360.crushlovecalculator.ui.lovetest

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basefragment.AbsBaseFragment
import com.hola360.crushlovecalculator.databinding.FragmentLoveTestBinding
import com.hola360.crushlovecalculator.ui.lovetest.adapter.LoveMethodAdapter

class LoveTestFragment: AbsBaseFragment<FragmentLoveTestBinding>() {

    private val adapter: LoveMethodAdapter by lazy {
        LoveMethodAdapter(object :LoveMethodAdapter.OnItemLoveTestClick{
            override fun onClick(position: Int) {
                onItemClick(position)
            }
        })
    }

    override fun initView() {
        binding!!.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding!!.loveTestRv.setHasFixedSize(true)
        binding!!.loveTestRv.layoutManager= GridLayoutManager(requireContext(), 3)
        binding!!.loveTestRv.adapter= adapter

    }

    private fun onItemClick(position: Int){
        when(position){
            PHOTO_TYPE->{
                findNavController().navigate(R.id.nav_test_photo)
            }
            NAME_TYPE->{
                findNavController().navigate(R.id.nav_test_name)
            }
            FINGERPRINT_TYPE->{
                findNavController().navigate(R.id.nav_test_fingerprint)
            }
            BIRTHDAY_TYPE->{
                findNavController().navigate(R.id.nav_test_birthday)
            }
            NATIONALITY_TYPE->{
                findNavController().navigate(R.id.nav_test_nationality)
            }
            BMI_TYPE->{
                findNavController().navigate(R.id.nav_test_bmi)
            }
            JOB_TYPE->{
                findNavController().navigate(R.id.nav_test_job)
            }
        }
    }
    override fun getLayout(): Int {
        return R.layout.fragment_love_test
    }

    companion object{
        const val PHOTO_TYPE=0
        const val NAME_TYPE=1
        const val FINGERPRINT_TYPE=2
        const val BIRTHDAY_TYPE=3
        const val NATIONALITY_TYPE=4
        const val BMI_TYPE=5
        const val JOB_TYPE=6
    }
}
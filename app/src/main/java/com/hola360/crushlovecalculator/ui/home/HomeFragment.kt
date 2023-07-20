package com.hola360.crushlovecalculator.ui.home

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hola360.crushlovecalculator.NavMainGraphDirections
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basefragment.BasePermissionFragment
import com.hola360.crushlovecalculator.data.model.ProfileModel
import com.hola360.crushlovecalculator.databinding.FragmentHomeBinding
import com.hola360.crushlovecalculator.utils.SystemUtils

class HomeFragment: BasePermissionFragment<FragmentHomeBinding>() {
    private var profileModel : ProfileModel? = null
    private val adapter: HomeAdapter by lazy {
        HomeAdapter(object : HomeAdapter.OnItemHomeClick {
            override fun onClick(position: Int) {
                onItemClick(position)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        profileModel =  dataPref.getUserProfileModel(false)
        binding!!.profileModel = profileModel
    }
    override fun initView() {
        binding!!.profile.setOnClickListener{
            findNavController().navigate(NavMainGraphDirections.actionToNavProfile(false))
        }
        binding!!.notification.setOnClickListener(){
            mainActivity.showComingSoon()
        }
        binding!!.rcvHome.setHasFixedSize(true)
        binding!!.rcvHome.layoutManager = GridLayoutManager(requireContext(), 3)
        binding!!.rcvHome.adapter = adapter
    }
    fun onItemClick(position: Int) {
        when (position) {
            0 -> {
                findNavController().navigate(NavMainGraphDirections.actionToNavLoveTest())
            }
            1 -> {
                mainActivity.showComingSoon()
            }
            2 -> {
                findNavController().navigate(R.id.nav_love_time)
            }
            3 -> {
                findNavController().navigate(R.id.nav_love_diary)
            }
            4 -> {
                if (!SystemUtils.hasPermissions(
                        requireContext(),
                        *SystemUtils.getStoragePermissions()
                    )
                ) {
                    storageResultLauncher.launch(SystemUtils.getStoragePermissions())
                } else {
                    findNavController().navigate(R.id.nav_my_photo)
                }
            }
            5 -> {
                findNavController().navigate(NavMainGraphDirections.actionGlobalEventFragment())
            }
            6 -> {
                mainActivity.showComingSoon()
            }
            7 -> {
                mainActivity.showComingSoon()
            }
            8 -> {
                mainActivity.showComingSoon()
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_home
    }

    override fun onPermissionResult() {
        if (SystemUtils.hasPermissions(requireContext(), *SystemUtils.getStoragePermissions())) {
            findNavController().navigate(R.id.nav_my_photo)
        } else {
            SystemUtils.showAlertPermissionNotGrant(binding!!, requireActivity())
        }
    }

    override fun initViewModel() {
    }

}
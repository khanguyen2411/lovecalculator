package com.hola360.crushlovecalculator.ui.lovetime.preview

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.databinding.FragmentLoveTimeBinding
import com.hola360.crushlovecalculator.ui.lovetime.base.BaseLoveTimeFragment
import com.hola360.crushlovecalculator.utils.snackbar.SnackBarType

class ThemePreviewFragment : BaseLoveTimeFragment<FragmentLoveTimeBinding>() {

    private lateinit var mViewModel: ThemePreviewViewModel
    private val args by lazy {
        ThemePreviewFragmentArgs.fromBundle(requireArguments())
    }

    override fun initView() {
        setToolbarMenu()
        setNavigationIcon()
        binding!!.apply {
            toolbar.apply {
                setNavigationOnClickListener { findNavController().navigateUp() }
                setOnMenuItemClickListener { item ->
                    run {
                        when (item.itemId) {
                            R.id.action_apply -> {
                                dataPref.setLoveBg(null)
                                mViewModel.installTheme()
                                findNavController().popBackStack(R.id.nav_love_time, false)
                                mainActivity.showSnackBar(
                                    SnackBarType.Success,
                                    getString(R.string.title_success),
                                    getString(R.string.theme_preview_apply_success_message)
                                )
                            }
                            R.id.action_favorite -> {
                                mViewModel.pushCountTheme(args.itemId)
                                menu.getItem(1)
                                    .setIcon(R.drawable.ic_favorite_fill_21)
                            }
                            else -> {}
                        }
                    }
                    true
                }
            }
        }
        binding!!.viewModel = mViewModel
        mViewModel.fetchLoveTime(true)
    }

    override fun getLayout(): Int {
        return R.layout.fragment_love_time
    }

    override fun onPermissionResult() {}

    override fun initViewModel() {
        val factory = ThemePreviewViewModel.Factory(mainActivity.application)
        mViewModel = ViewModelProvider(this, factory)[ThemePreviewViewModel::class.java]
    }

    override fun setNavigationIcon() {
        binding!!.toolbar.setNavigationIcon(R.drawable.ic_my_photo_close)
    }

    override fun setToolbarMenu() {
        binding!!.toolbar.inflateMenu(R.menu.menu_preview_theme)
    }
}
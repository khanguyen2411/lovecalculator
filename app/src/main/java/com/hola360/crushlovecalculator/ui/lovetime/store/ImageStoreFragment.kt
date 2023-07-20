package com.hola360.crushlovecalculator.ui.lovetime.store

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basefragment.AbsBaseFragment
import com.hola360.crushlovecalculator.databinding.FragmentImageStoreBinding
import com.hola360.crushlovecalculator.ui.lovetime.store.adapter.StoreViewPagerAdapter
import com.hola360.crushlovecalculator.ui.lovetime.store.favorite.FavoriteImageFragment
import com.hola360.crushlovecalculator.ui.lovetime.store.hot.HotImageFragment
import com.hola360.crushlovecalculator.ui.lovetime.store.newbg.NewImageFragment
import com.hola360.crushlovecalculator.utils.SystemUtils

class ImageStoreFragment : AbsBaseFragment<FragmentImageStoreBinding>() {
    private var mAdapter: StoreViewPagerAdapter? = null
    private val fragments = mutableListOf<Fragment>()
    private val titles = arrayOf(
        R.string.tab_hot_trend,
        R.string.tab_new,
        R.string.tab_favorite
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPage()
    }

    override fun initView() {
        binding!!.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        binding!!.viewPager.apply {
            adapter = mAdapter
            offscreenPageLimit = fragments.size
        }
        addVerticalDivider()
        TabLayoutMediator(
            binding!!.tabLayout, binding!!.viewPager
        ) { tab, position ->
            tab.text = getString(titles[position])
        }.attach()
        binding!!.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 2) {
                    (fragments[position] as FavoriteImageFragment).fetchData()
                }
            }
        })
    }

    private fun addVerticalDivider() {
        val root: View = binding!!.tabLayout.getChildAt(0)
        if (root is LinearLayout) {
            root.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            val drawable = GradientDrawable()
            drawable.setColor(ContextCompat.getColor(mainActivity, R.color.colorAccent))
            drawable.setSize(
                SystemUtils.convertPixelsToDp(
                    resources.getDimension(R.dimen.dp_2),
                    mainActivity
                ).toInt(), 1
            )
            root.dividerPadding =
                SystemUtils.convertPixelsToDp(resources.getDimension(R.dimen.dp_8), mainActivity)
                    .toInt()
            root.dividerDrawable = drawable
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_image_store
    }

    private fun initPage() {
        fragments.clear()
        fragments.add(HotImageFragment())
        fragments.add(NewImageFragment())
        fragments.add(FavoriteImageFragment())
        mAdapter = StoreViewPagerAdapter(this, fragments)

    }
}
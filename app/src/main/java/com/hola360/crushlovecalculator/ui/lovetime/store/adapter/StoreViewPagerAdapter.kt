package com.hola360.crushlovecalculator.ui.lovetime.store.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class StoreViewPagerAdapter(fragment: Fragment, private val fragments: MutableList<Fragment>) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
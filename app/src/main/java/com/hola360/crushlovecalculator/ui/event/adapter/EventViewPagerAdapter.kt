package com.hola360.crushlovecalculator.ui.event.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class EventViewPagerAdapter(fragment: Fragment, private val fragments: MutableList<Fragment>) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
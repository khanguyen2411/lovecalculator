package com.hola360.crushlovecalculator.ui.event

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
import com.hola360.crushlovecalculator.data.model.event.EventModel
import com.hola360.crushlovecalculator.databinding.FragmentEventBinding
import com.hola360.crushlovecalculator.ui.event.adapter.EventViewPagerAdapter
import com.hola360.crushlovecalculator.ui.event.common.CommonEventFragment
import com.hola360.crushlovecalculator.ui.event.eventdialog.EventDiaLog
import com.hola360.crushlovecalculator.ui.event.myevent.MyEventFragment
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.setSafeMenuClickListener

class EventFragment : AbsBaseFragment<FragmentEventBinding>(), EventDiaLog.OnDoneClick {
    private var mAdapter: EventViewPagerAdapter? = null
    private var fragments = mutableListOf<Fragment>()
    private var eventModel = EventModel(0, "", "", "", "", "", 0L, 2, 0L)
    private val titles = arrayOf(
        R.string.tab_common_event,
        R.string.tab_my_event,
    )

    private val eventDialog by lazy {
        EventDiaLog.create(isAdd = true,"Add event",eventModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPage()
    }

    override fun initView() {
        mAdapter = EventViewPagerAdapter(this, fragments)
        binding!!.apply {
            viewPager.apply {
                offscreenPageLimit = fragments.size
                adapter = mAdapter
            }
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            toolbar.setSafeMenuClickListener { menuItem ->
                if (menuItem!!.itemId == R.id.action_add_event) {
                    if (!eventDialog.isAdded) {
                        eventDialog.setOnDoneClick(this@EventFragment)
                        eventDialog.show(requireActivity().supportFragmentManager, "addEvent")
                    }
                }
            }
        }

        addVerticalDivider()
        TabLayoutMediator(binding!!.tabLayout, binding!!.viewPager) { tab, position ->
            tab.text = getString(titles[position])
        }.attach()

        binding!!.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 1) {
                    (fragments[position] as MyEventFragment).fetchData()
                }
            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.fragment_event
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

    fun initPage() {
        fragments.clear()
        fragments.add(CommonEventFragment())
        fragments.add(MyEventFragment())
    }

    override fun onDoneClick(eventModel: EventModel) {
        (fragments[1] as MyEventFragment).fetchData()
        findNavController().navigate(
            EventFragmentDirections.actionEventFragmentToEventDetailFragment(
                eventModel
            )
        )
    }
}
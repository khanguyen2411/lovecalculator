package com.hola360.crushlovecalculator.ui.lovediary.calendar

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basefragment.BaseViewModelFragment
import com.hola360.crushlovecalculator.data.model.diary.DiaryModel
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.databinding.FragmentCalendarBinding
import com.hola360.crushlovecalculator.dialog.lovetimedialog.DatePickerDiaLog
import com.hola360.crushlovecalculator.ui.lovediary.calendar.adapter.CalendarAdapter
import com.hola360.crushlovecalculator.ui.lovediary.calendar.adapter.DiaryAdapter
import com.hola360.crushlovecalculator.ui.lovediary.calendar.adapter.ViewPagerCalendarAdapter
import com.hola360.crushlovecalculator.ui.lovediary.calendar.utils.GenerateCalendarStatus
import com.hola360.crushlovecalculator.ui.lovediary.calendar.utils.GeneratedCalendar
import com.hola360.crushlovecalculator.ui.lovediary.model.Date
import com.hola360.crushlovecalculator.utils.Constants
import com.hola360.crushlovecalculator.utils.SystemUtils
import com.hola360.crushlovecalculator.utils.clickWithDebounce
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
class CalendarFragment : BaseViewModelFragment<FragmentCalendarBinding>(),
    CalendarAdapter.OnItemClickListener, DatePickerDiaLog.OnDatePickerListener, DiaryAdapter.OnDiaryClickListener {

    private lateinit var mViewModel: CalendarViewModel
    private val mDiaryAdapter = DiaryAdapter()  
    private lateinit var vpAdapter: ViewPagerCalendarAdapter
    private var currentDate = System.currentTimeMillis()
    private val mCalendar = GregorianCalendar()
    private var numberOfMonth = 0
    private var isInitView = false

    private val datePickerDiaLog: DatePickerDiaLog by lazy {
        DatePickerDiaLog.create(true, getString(R.string.string_pick_diary_date))
    }

    override fun initView() {
        binding!!.apply {
            viewModel = mViewModel
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

            vpCalendar.apply {
                adapter = vpAdapter
                vpAdapter.listener = this@CalendarFragment
                offscreenPageLimit = 1
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        if (!isInitView) {
                            val runnable = Runnable {
                                vpCalendar.setCurrentItem(
                                    Constants.CALENDAR_MIDDLE_MONTH_POSITION,
                                    false
                                )
                            }
                            handler.postDelayed(runnable, 100)
                            isInitView = true
                        } else {
                            if (position == numberOfMonth - 1) {
                                mViewModel.getNextMonth()
                            }

                            if (position == 0) {
                                mViewModel.getPreviousMonth()
                            }
                        }
                    }
                })
            }

            tvDate.apply {
                text = setDate(currentDate)
                clickWithDebounce {
                    if (!datePickerDiaLog.isAdded) {
                        datePickerDiaLog.apply {
                            onDatePickerListener = this@CalendarFragment
                            setCurDate(currentDate)
                        }
                        datePickerDiaLog.show(mainActivity.supportFragmentManager, "PickDiaryDate")
                    }
                }
            }

            tvPickDate.text = setDate(currentDate)

            tvToday.clickWithDebounce {
                mViewModel.generateData(
                    mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH) + 1,
                    mCalendar
                )
                currentDate = System.currentTimeMillis()
                val mTime = setDate(currentDate)
                tvDate.text = mTime
                tvPickDate.text = mTime
                fetchDiary(time = currentDate)
                vpCalendar.setCurrentItem(Constants.CALENDAR_MIDDLE_MONTH_POSITION, false)
            }

            rvDiary.apply {
                layoutManager =
                    LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
                adapter = mDiaryAdapter
            }
        }
    }

    fun setDate(date: Long): String {
        currentDate = date
        return SystemUtils.getDiaryDate(date)
    }

    override fun getLayout(): Int {
        return R.layout.fragment_calendar
    }

    override fun initViewModel() {
        val factory = CalendarViewModel.Factory(mainActivity.application)
        mViewModel = ViewModelProvider(this, factory)[CalendarViewModel::class.java]
        vpAdapter = ViewPagerCalendarAdapter(mainActivity)
        mCalendar.timeInMillis = System.currentTimeMillis()

        mViewModel.mDiaryList.observe(this) {
            it?.let {
                when (it.loadDataStatus) {
                    LoadDataStatus.SUCCESS -> {
                        val data = (it as DataResponse.DataSuccessResponse).body
                        mDiaryAdapter.updateData(data)
                    }
                    LoadDataStatus.ERROR -> {
                        mDiaryAdapter.clearData()
                    }
                    else -> {}
                }
            }
        }

        mViewModel.mMonthList.observe(this) {
            it?.let {
                when (it.status) {
                    GenerateCalendarStatus.New -> {
                        val data = (it as GeneratedCalendar.NewData).body
                        numberOfMonth = data.size
                        vpAdapter.updateData(data)
                        fetchDiary(time = currentDate)
                    }
                    GenerateCalendarStatus.Next -> {
                        val data = (it as GeneratedCalendar.NextData).body
                        vpAdapter.addNextMonth(data)
                        numberOfMonth += data.size
                    }
                    GenerateCalendarStatus.Previous -> {
                        val data = (it as GeneratedCalendar.PreviousData).body
                        vpAdapter.addPreviousMonth(data)
                        numberOfMonth += data.size
                    }
                    GenerateCalendarStatus.NoData -> {}
                }
            }
        }

        mViewModel.generateData()
    }

    fun fetchDiary(date: Date? = null, time: Long? = null) {
        val calendar = GregorianCalendar()

        if (date != null) {
            calendar.set(date.year, date.month - 1, date.date, 0, 0, 0)
            currentDate = calendar.timeInMillis
        }

        if (time != null) {
            calendar.apply {
                timeInMillis = time
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            currentDate = calendar.timeInMillis
        }

        binding!!.apply {
            val mText = SystemUtils.getDiaryDate(currentDate)
            tvDate.text = mText
            tvPickDate.text = mText
        }
        mViewModel.fetchDiary(currentDate)
    }

    override fun onItemClickListener(date: Date) {
        fetchDiary(date = date)
    }

    override fun onDatePicked(time: Long) {
        val calendar = GregorianCalendar()
        calendar.timeInMillis = time
        mViewModel.generateData(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar
        )
        binding!!.vpCalendar.setCurrentItem(Constants.CALENDAR_MIDDLE_MONTH_POSITION, false)
        fetchDiary(time = time)
    }

    override fun onDismiss() {

    }

    override fun onDiaryClickListener(diaryModel: DiaryModel) {

    }
}
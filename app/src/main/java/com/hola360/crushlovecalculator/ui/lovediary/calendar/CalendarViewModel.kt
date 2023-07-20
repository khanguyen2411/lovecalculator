package com.hola360.crushlovecalculator.ui.lovediary.calendar

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.data.model.diary.DiaryModel
import com.hola360.crushlovecalculator.data.repository.DiaryRepository
import com.hola360.crushlovecalculator.data.utils.DataResponse
import com.hola360.crushlovecalculator.data.utils.LoadDataStatus
import com.hola360.crushlovecalculator.ui.lovediary.calendar.utils.GeneratedCalendar
import com.hola360.crushlovecalculator.ui.lovediary.model.Date
import com.hola360.crushlovecalculator.ui.lovediary.model.Month
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

@RequiresApi(Build.VERSION_CODES.N)
@Suppress("DeferredResultUnused")
class CalendarViewModel(private val app: Application) : ViewModel() {

    private val repository = DiaryRepository(app)

    val mMonthList =
        MutableLiveData<GeneratedCalendar<MutableList<Month>>>(GeneratedCalendar.NoData())

    val mDiaryList =
        MutableLiveData<DataResponse<MutableList<DiaryModel>>>(DataResponse.DataEmptyResponse())

    val isLoading: LiveData<Boolean> = Transformations.map(mDiaryList) {
        mDiaryList.value!!.loadDataStatus == LoadDataStatus.LOADING
    }

    val isEmpty: LiveData<Boolean> = Transformations.map(mDiaryList) {
        mDiaryList.value!!.loadDataStatus == LoadDataStatus.ERROR
    }

    private lateinit var mDate: Date
    private var mCurrentYear = 0
    private var mCurrentMonth = 0
    private var mCurrentDay = 0

    private var mMonth = 0
    private var mYear = 0

    private var mLatestMonth = 0
    private var mLatestYear = 0

    private var mOldestMonth = 0
    private var mOldestYear = 0

    private var mEndBelong = false
    private var mStartBelong = false
    private var mStartDay = 0
    private var mEndDay = 0

    init {
        val calendar = Calendar.getInstance()
        mCurrentYear = calendar.get(Calendar.YEAR)
        mCurrentMonth = calendar.get(Calendar.MONTH) + 1
        mCurrentDay = calendar.get(Calendar.DAY_OF_MONTH)

        this.mYear = mCurrentYear
        this.mMonth = mCurrentMonth

        this.mLatestMonth = mCurrentMonth
        this.mLatestYear = mCurrentYear

        this.mOldestMonth = mCurrentMonth
        this.mOldestYear = mCurrentYear
    }

    fun fetchDiary(date: Long) {
        viewModelScope.launch {
            mDiaryList.value = DataResponse.DataLoadingResponse()
            try {
                val diaryList = repository.getDiaryByDate(date)
                if (diaryList != null && diaryList.isNotEmpty()) {
                    mDiaryList.value = DataResponse.DataSuccessResponse(diaryList.toMutableList())
                } else {
                    mDiaryList.value = DataResponse.DataErrorResponse(null)
                }
            } catch (ex: Exception) {
                mDiaryList.value = DataResponse.DataErrorResponse(null)
            }
        }
    }

    fun generateData(
        year: Int = mCurrentYear,
        month: Int = mCurrentMonth,
        selectedDate: GregorianCalendar? = null
    ) {
        viewModelScope.launch {
            try {
                val data = initMonthList(year, month, type = NOW, selectedDate = selectedDate)
                mMonthList.value = GeneratedCalendar.NewData(checkDiary(data))
            } catch (ex: Exception) { }
        }
    }

    fun getNextMonth() {
        viewModelScope.launch {
            val data = initMonthList(mLatestYear, mLatestMonth, NEXT)
            mMonthList.value = GeneratedCalendar.NextData(checkDiary(data))
        }
    }

    fun getPreviousMonth() {
        viewModelScope.launch {
            try {
                val data = initMonthList(mOldestYear, mOldestMonth, PREVIOUS)
                mMonthList.value = GeneratedCalendar.PreviousData(checkDiary(data))
            } catch (ex: Exception) { }
        }
    }

    private fun checkDiary(data : MutableList<Month>) : MutableList<Month> {
        val checkHasDiary = CoroutineScope(Dispatchers.Default).async {
            data.forEach {
                if (it.year <= mCurrentYear && it.month <= mCurrentMonth) {
                    async {
                        it.days.forEach { day ->
                            if (day.date <= mCurrentDay) {
                                async {
                                    day.hasDiary =
                                        repository.checkDateHasDiary(
                                            convertToMillis(
                                                day.year,
                                                day.month,
                                                day.date
                                            )
                                        )
                                }
                            }
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            checkHasDiary.await()
        }
        return data
    }

    fun initMonthList(
        year: Int,
        month: Int,
        type: Int = NOW,
        selectedDate: GregorianCalendar? = null
    ): MutableList<Month> {
        val list = mutableListOf<Month>()

        var sRange = 0
        var eRange = 0

        when (type) {
            NOW -> {
                sRange = -1
                eRange = 1
            }
            PREVIOUS -> {
                sRange = -2
                eRange = -1
            }
            NEXT -> {
                sRange = 1
                eRange = 2
            }
        }

        for (i in sRange..eRange) {
            val mMonth = Month()
            mMonth.apply {
                var fetchYear = year
                var fetchMonth = month

                if(month + i == 0){
                    fetchMonth = 12
                    fetchYear = year - 1
                } else {
                    if(month + i < 0){
                        fetchMonth = 11
                        fetchYear = year - 1
                    } else {
                        if(month + i == 13){
                            fetchMonth = 1
                            fetchYear = year + 1
                        } else {
                            if(month + i == 14){
                                fetchMonth = 2
                                fetchYear = year + 1
                            } else {
                                fetchMonth += i
                            }
                        }
                    }
                }

                days = initDateList(fetchYear, fetchMonth, selectedDate)
                this.month = fetchMonth
                this.year = fetchYear
            }
            list.add(mMonth)
        }

        when (type) {
            NOW -> {
                mOldestMonth = list[0].month
                mOldestYear = list[0].year

                mLatestYear = list[list.size - 1].year
                mLatestMonth = list[list.size - 1].month
            }
            PREVIOUS -> {
                mOldestMonth = list[0].month
                mOldestYear = list[0].year
            }
            NEXT -> {
                mLatestYear = list[list.size - 1].year
                mLatestMonth = list[list.size - 1].month
            }
        }

        return list
    }

    fun initDateList(
        year: Int,
        month: Int,
        selectedDate: GregorianCalendar? = null,
        type: Int = NOW
    ): MutableList<Date> {
        val list = mutableListOf<Date>()

        var tempYear = year
        when (type) {
            NOW -> {
                tempYear = mYear
                mYear = year
                mMonth = month
            }
            NEXT -> {
                tempYear = mLatestYear
                mLatestYear = year
                mLatestMonth = month
            }
            PREVIOUS -> {
                tempYear = mOldestMonth
                mOldestMonth = month
                mOldestYear = year
            }
        }

        val endDate = if (year - 1 == tempYear || month == 1) {
            getDays(year - 1, 12)
        } else {
            getDays(year, month - 1)
        }

        val days: Int = getDays(year, month)

        val dayOfWeek: Int =
            getWeekDay(year, month)

        var selfDaysEndWeek = 0
        mStartBelong = true
        if (dayOfWeek != 0) {
            val startDate =
                endDate - dayOfWeek + 1
            var i = startDate
            var j = 0
            while (i <= endDate) {
                mDate = Date(year = year, month = month - 1, weekDay = i)
                mDate.date = i
                if (startDate == i) {
                    mStartBelong = false
                    mStartDay = mDate.date
                }
                mDate.isSelfMonthDate = false
                mDate.weekDay = WEEK_DAY_ROW[j]
                list.add(mDate)
                i++
                j++
            }
        }

        run {
            var i = 1
            var j = dayOfWeek
            while (i <= days) {
                mDate = Date(year = year, month = month, weekDay = i)
                mDate.date = i
                if (mStartBelong && i == 1) {
                    mStartDay = mDate.date
                }
                if (i == days) {
                    mEndDay = mDate.date
                }
                mDate.isSelfMonthDate = true
                if (j >= 7) {
                    j = 0
                }
                selfDaysEndWeek = j
                mDate.weekDay = WEEK_DAY_ROW[j]
                if (year == mCurrentYear && month == mCurrentMonth && i == mCurrentDay) {
                    mDate.isNowDate = true
                }
                if(selectedDate != null){
                    if (i == selectedDate.get(Calendar.DAY_OF_MONTH) &&
                        month == selectedDate.get(Calendar.MONTH) + 1 &&
                        year == selectedDate.get(Calendar.YEAR)) {
                        mDate.isSelected = true
                    }
                }
                list.add(mDate)
                i++
                j++
            }
        }

        mEndBelong = true
        var i = 1
        var j = selfDaysEndWeek + 1
        while (i < 7) {
            if (j >= 7) {
                break
            }
            mEndBelong = false
            mDate = Date(year = year, month = month + 1, weekDay = i)
            if (mDate.month > 12) {
                mDate.year = year + 1
                mDate.month = 1
            }
            mDate.date = i
            mDate.isSelfMonthDate = false
            mDate.weekDay = WEEK_DAY_ROW[j]
            list.add(mDate)
            mEndDay = mDate.year * 10000 + mDate.month * 100 + i
            i++
            j++
        }

        return list
    }

    private fun getDays(year: Int, month: Int): Int {
        var days = 0
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            if (month in 1..12) {
                days = leapYearMonthDay[month - 1]
            }
        } else {
            if (month in 1..12) {
                days = commonYearMonthDay[month - 1]
            }
        }
        return days
    }

    private fun getWeekDay(year: Int, month: Int): Int {
        val dayOfWeek: Int
        var goneYearDays = 0
        var thisYearDays = 0

        for (i in 1900 until year) {
            goneYearDays = if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {
                goneYearDays + 366
            } else {
                goneYearDays + 365
            }
        }
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            for (i in 0 until month - 1) {
                thisYearDays += leapYearMonthDay[i]
            }
        } else {
            for (i in 0 until month - 1) {
                thisYearDays += commonYearMonthDay[i]
            }
        }
        dayOfWeek = (goneYearDays + thisYearDays + 1) % 7
        return dayOfWeek
    }

    fun convertToMillis(year: Int, month: Int, day: Int): Long {
        val calendar = GregorianCalendar()
        calendar.set(year, month - 1, day, 0, 0, 0)
        return calendar.timeInMillis
    }

    companion object {
        val WEEK_DAY_ROW = arrayOf(0, 1, 2, 3, 4, 5, 6)
        val commonYearMonthDay = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val leapYearMonthDay = intArrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        const val NOW = 1
        const val PREVIOUS = 2
        const val NEXT = 3
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
                return CalendarViewModel(app) as T
            }

            throw IllegalArgumentException(app.resources.getString(R.string.unable_create_viewmodel))
        }
    }
}
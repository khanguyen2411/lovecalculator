package com.hola360.crushlovecalculator.ui.event.eventdialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseViewModelBottomSheetDiaLog
import com.hola360.crushlovecalculator.data.model.event.EventModel
import com.hola360.crushlovecalculator.databinding.LayoutEventDialogBinding
import com.hola360.crushlovecalculator.utils.*
import java.lang.reflect.Field
import java.util.*


class EventDiaLog : BaseViewModelBottomSheetDiaLog<LayoutEventDialogBinding>(),
    View.OnClickListener {

    private var showCalculationType = true
    private var showDatePicker = true
    private val todayCalendar = Calendar.getInstance()
    private var calendar: GregorianCalendar? = null
    var calType: Int? = 0
    var maximum = -1
    private lateinit var viewModel: EventDiaLogViewModel
    private var title: String? = null
    private var eventModel: EventModel? = null
    private lateinit var onDoneClick: OnDoneClick
    private var isAdd = false

    fun setOnDoneClick(onDoneClick: OnDoneClick) {
        this.onDoneClick = onDoneClick
    }

    override fun onDismiss() {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { _it ->
                val behavior = BottomSheetBehavior.from(_it)
                setupFullHeight(_it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isDraggable = false
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun initView() {
        showCalculationType = true
        showDatePicker = true
        maximum = eventModel!!.calType
        binding!!.yearPicker.setWrapSelectorWheel(false)
        hideKeyBoard(binding!!.parent)
        title = requireArguments().getString(KEY_TITLE)
        isAdd = requireArguments().getBoolean(KEY_ADD)
        calType = eventModel!!.calType

        binding!!.apply {
            titleEvent.text = title
            nameEmpty = false
            isShowDate = true
            isShowType = true
            close.setOnClickListener { onClickClose() }
            layoutPickDate.setOnClickListener(this@EventDiaLog)
            layoutSelectType.setOnClickListener(this@EventDiaLog)
            etNameEvent.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty()) {
                        nameEmpty = false
                    }
                }
            })
            done.setOnClickListener(this@EventDiaLog)
            done2.setOnClickListener(this@EventDiaLog)
            btnDayLeft.setOnClickListener(this@EventDiaLog)
            btnDayCount.setOnClickListener(this@EventDiaLog)
            btnDayAnnually.setOnClickListener(this@EventDiaLog)
        }
        if (isAdd) {
            setCurDate(System.currentTimeMillis())
        } else {
            setCurDate(eventModel!!.date)
        }

        setupMonthPicker()
        setupYearPicker()
        setupDayPicker()

        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    fun hideKeyBoard(view: View) {
        if (view !is EditText) {
            view.setOnTouchListener { v, event ->
                SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
                false
            }
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                hideKeyBoard(innerView)
            }
        }
    }

    private fun onClickClose() {
        dismiss()
        SystemUtils.hideSoftKeyboard(requireActivity(), binding!!.root)
    }

    private fun setupDayPicker() {
        binding!!.dayPicker.minValue = 1
        binding!!.dayPicker.setOnValueChangedListener { _, _, _ ->
            setupMaxDay()
            setTimeText()
            binding!!.date.visibility = View.GONE
        }
    }

    private fun setTimeText() {
        binding!!.dayOfWeeks.setDayOfWeekstEventTime(calendar!!.time.time)
        binding!!.month.setMonthEventTime(calendar!!.time.time)
        binding!!.day.text = binding!!.dayPicker.value.toString() + ","
        binding!!.year.text = binding!!.yearPicker.value.toString()
        binding!!.date.visibility = View.GONE
    }

    private fun setupYearPicker() {
        when(maximum){
            1->{
                binding!!.yearPicker.maxValue = Calendar.getInstance().get(Calendar.YEAR) + 50
                binding!!.yearPicker.minValue = Calendar.getInstance().get(Calendar.YEAR)
            }
            2->{
                binding!!.yearPicker.maxValue = Calendar.getInstance().get(Calendar.YEAR)
                binding!!.yearPicker.minValue = 1900
            }
            3->{
                binding!!.yearPicker.isEnabled = false
                binding!!.yearPicker.maxValue = Calendar.getInstance().get(Calendar.YEAR)+50
                binding!!.yearPicker.minValue = 1900
            }
        }
        binding!!.yearPicker.setOnValueChangedListener { _, _, _ ->
            setupMaxDay()
            setTimeText()
            binding!!.date.visibility = View.GONE
        }
    }

    private fun setupMonthPicker() {
        val months = resources.getStringArray(R.array.months)
        binding!!.monthPicker.maxValue = 11
        binding!!.monthPicker.minValue = 0
        binding!!.monthPicker.wrapSelectorWheel = true
        binding!!.monthPicker.displayedValues = months
        binding!!.monthPicker.setOnValueChangedListener { _, _, _ ->
            setupMaxDay()
            setTimeText()
            binding!!.date.visibility = View.GONE
        }
    }

    private fun setupMaxDay() {
        val maxValue = getNumberDayOfMonth()
        binding!!.dayPicker.minValue = 1
        binding!!.dayPicker.maxValue = maxValue
    }

    fun setCurDate(curDate: Long) {
        calendar = GregorianCalendar().apply {
            timeInMillis = curDate
        }
    }

    private fun getNumberDayOfMonth(): Int {
        calendar!!.set(Calendar.YEAR, binding!!.yearPicker.value)
        calendar!!.set(Calendar.MONTH, binding!!.monthPicker.value)
        return calendar!!.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun isAnnually() {
        calendar = GregorianCalendar(
            binding!!.yearPicker.value,
            binding!!.monthPicker.value,
            binding!!.dayPicker.value
        )
    }

    fun isDayCount(): Boolean {
        calendar = GregorianCalendar(
            binding!!.yearPicker.value,
            binding!!.monthPicker.value,
            binding!!.dayPicker.value
        )
        return (calendar!!.get(Calendar.YEAR) < todayCalendar.get(Calendar.YEAR)) ||
                ((calendar!!.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR)) &&
                        (calendar!!.get(Calendar.DAY_OF_YEAR) <= todayCalendar.get(Calendar.DAY_OF_YEAR)))
    }

    fun isDateLeft(): Boolean {
        calendar = GregorianCalendar(
            binding!!.yearPicker.value,
            binding!!.monthPicker.value,
            binding!!.dayPicker.value
        )
        return (calendar!!.get(Calendar.YEAR) > todayCalendar.get(Calendar.YEAR)) ||
                ((calendar!!.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR)) &&
                        (calendar!!.get(Calendar.DAY_OF_YEAR) >= todayCalendar.get(Calendar.DAY_OF_YEAR)))
    }

    override fun onStart() {
        super.onStart()
        binding!!.monthPicker.value = calendar!!.get(Calendar.MONTH)
        binding!!.yearPicker.value =
            calendar!!.get(Calendar.YEAR).coerceAtMost(binding!!.yearPicker.maxValue)
        setupMaxDay()
        binding!!.dayPicker.value = calendar!!.get(Calendar.DAY_OF_MONTH)
        binding!!.dayPicker.value = calendar!!.get(Calendar.DAY_OF_MONTH)
        if (!isAdd) {
            binding!!.date.setEventTime(eventModel!!.date)
        } else {
            binding!!.date.setEventTime(calendar!!.time.time)
        }
        binding!!.etNameEvent.setText(eventModel!!.title)
        checkType(eventModel!!.calType)
    }


    override fun getLayout(): Int {
        return R.layout.layout_event_dialog
    }


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.layoutSelectType -> {
                if (showCalculationType) {
                    showCalculationType = false
                    binding!!.isShowType = false
                } else {
                    showCalculationType = true
                    binding!!.isShowType = true
                }
            }
            R.id.layoutPickDate -> {
                if (showDatePicker) {
                    showDatePicker = false
                    binding!!.isShowDate = false
                } else {
                    showDatePicker = true
                    binding!!.isShowDate = true
                }
            }
            R.id.done, R.id.done_2 -> {
                if (!isAdd) {
                    editEvent()
                } else {
                    addEvent()
                }
            }
            R.id.btnDayLeft -> {
                maximum = 1
                setupYearPicker()
                checkDayLeft()
                setTimeText()
            }
            R.id.btnDayCount -> {
                maximum = 2
                setupYearPicker()
                checkDayCount()
                setTimeText()
            }
            R.id.btnDayAnnually -> {
                maximum = 3
                setupYearPicker()
                checkDayAnnually()
                disableAnnually()

            }
        }
    }

    private fun checkDayLeft() {
        calType = 1
        binding!!.btnDayCountShow.text = getString(R.string.string_event_day_left)
        binding!!.selectedDayLeft.visibility = View.VISIBLE
        binding!!.selectedDayCount.visibility = View.GONE
        binding!!.selectedDayAnnually.visibility = View.GONE
        binding!!.yearPicker.isEnabled = true
    }

    fun checkDayCount() {
        calType = 2
        binding!!.btnDayCountShow.text = getString(R.string.string_event_day_count)
        binding!!.selectedDayLeft.visibility = View.GONE
        binding!!.selectedDayCount.visibility = View.VISIBLE
        binding!!.selectedDayAnnually.visibility = View.GONE
        binding!!.yearPicker.isEnabled = true
    }

    fun checkDayAnnually() {
        calType = 3
        binding!!.btnDayCountShow.text = getString(R.string.string_event_day_annually)
        binding!!.selectedDayLeft.visibility = View.GONE
        binding!!.selectedDayCount.visibility = View.GONE
        binding!!.selectedDayAnnually.visibility = View.VISIBLE
    }

    fun disableAnnually() {
        setCurDate(System.currentTimeMillis())
        binding!!.yearPicker.value =
            calendar!!.get(Calendar.YEAR).coerceAtMost(binding!!.yearPicker.maxValue)
        setupMaxDay()
        binding!!.yearPicker.isEnabled = false
        setTimeText()
    }

    private fun checkType(type: Int) {
        when (type) {
            1 -> {
                checkDayLeft()
            }
            2 -> {
                checkDayCount()
            }
            else -> {
                checkDayAnnually()
            }
        }
    }

    private fun editEvent() {
        val id = eventModel!!.eventId
        var eventName = eventModel!!.title
        var date = eventModel!!.date
        var isCheck = false
        if (binding!!.etNameEvent.text!!.trim().isEmpty()) {
            binding!!.nameEmpty = true
            focusEdittext(binding!!.etNameEvent)
            return
        } else {
            eventName = binding!!.etNameEvent.text.toString().trim()
            if (calType == 1) {
                if (isDateLeft()) {
                    binding!!.date.setEventTime(calendar!!.time.time)
                    date = calendar!!.time.time
                    isCheck = true
                } else {
                    ToastUtils.getInstance(requireActivity())
                        .showToast(getString(R.string.string_event_not_select_before_date))
                }
            }
            if (calType == 2) {
                if (isDayCount()) {
                    binding!!.date.setEventTime(calendar!!.time.time)
                    date = calendar!!.time.time
                    isCheck = true
                } else {
                    ToastUtils.getInstance(requireActivity())
                        .showToast(getString(R.string.string_event_not_select_after_date))
                }
            }
            if (calType == 3) {
                isAnnually()
                binding!!.date.setEventTime(calendar!!.time.time)
                date = calendar!!.time.time
                isCheck = true
            }
        }
        if (date != 0L && isCheck) {
            viewModel.editEvent(id!!, eventName, date, calType!!)
        }
    }

    private fun addEvent() {
        var title = eventModel!!.title
        var date = eventModel!!.date
        if (binding!!.etNameEvent.text!!.trim().isEmpty()) {
            binding!!.nameEmpty = true
            focusEdittext(binding!!.etNameEvent)
            return
        } else {
            title = binding!!.etNameEvent.text.toString().trim()
            if (calType == 1) {
                if (isDateLeft()) {
                    binding!!.date.setEventTime(calendar!!.time.time)
                    date = calendar!!.time.time
                } else {
                    ToastUtils.getInstance(requireActivity())
                        .showToast(getString(R.string.string_event_not_select_before_date))
                }
            }
            if (calType == 2) {
                if (isDayCount()) {
                    binding!!.date.setEventTime(calendar!!.time.time)
                    date = calendar!!.time.time
                } else {
                    ToastUtils.getInstance(requireActivity())
                        .showToast(getString(R.string.string_event_not_select_after_date))
                }
            }
            if (calType == 3) {
                isAnnually()
                binding!!.date.setEventTime(calendar!!.time.time)
                date = calendar!!.time.time
            }
        }

        if (date != 0L) {
            viewModel.addEvent(title, date, calType!!)
        }
    }

    fun focusEdittext(editText: EditText) {
        editText.requestFocus()
        SystemUtils.showSoftKeyboard(requireContext(), editText)
    }

    companion object {
        private const val KEY_ADD = "Key_add"
        private const val KEY_TITLE = "Key_title"
        private const val KEY_DATA = "Key_data"
        fun create(isAdd: Boolean, title: String, eventModel: EventModel): EventDiaLog {
            val bottomSheetDialog = EventDiaLog()
            val bundle = Bundle()
            bundle.putBoolean(KEY_ADD, isAdd)
            bundle.putString(KEY_TITLE, title)
            bundle.putParcelable(KEY_DATA, eventModel)
            bottomSheetDialog.arguments = bundle
            return bottomSheetDialog
        }
    }

    override fun initViewModel() {
        val factory = EventDiaLogViewModel.Factory(mainActivity.application)
        viewModel = ViewModelProvider(this, factory)[EventDiaLogViewModel::class.java]
        eventModel = requireArguments().getParcelable(KEY_DATA)

        viewModel.isDone.observe(this) {
            it?.let {
                if (it) {
                    onDoneClick.onDoneClick(viewModel.mEventModel!!)
                    dismiss()
                }
            }
        }
    }

    interface OnDoneClick {
        fun onDoneClick(eventModel: EventModel)
    }
}
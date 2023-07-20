package com.hola360.crushlovecalculator.dialog.lovetimedialog

import android.os.Bundle
import com.hola360.crushlovecalculator.R
import com.hola360.crushlovecalculator.base.basedialog.BaseBottomSheetDialog
import com.hola360.crushlovecalculator.databinding.DialogDatePickerBinding
import com.hola360.crushlovecalculator.utils.ToastUtils
import java.util.*

class DatePickerDiaLog : BaseBottomSheetDialog<DialogDatePickerBinding>() {
    private val todayCalendar = Calendar.getInstance()
    private var calendar: GregorianCalendar? = null
    var onDatePickerListener: OnDatePickerListener? = null
    var title: String? = null
    var isBirthDay = false
    var isDiary = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = requireArguments().getString(KEY_TITLE)
        isBirthDay = requireArguments().getBoolean(KEY_IS_BIRTHDAY)
        isDiary = requireArguments().getBoolean(KEY_IS_DIARY)
    }

    override fun initView() {
        binding!!.btClose.setOnClickListener {
            onDatePickerListener?.onDismiss()
            dismiss()
        }

        binding!!.set.apply {
            if (isDiary) {
                text = resources.getString(R.string.string_select)
            }
            setOnClickListener {
                if (isValidDate()) {
                    onDatePickerListener?.onDatePicked(calendar!!.time.time)
                    dismiss()
                } else {
                    if (isDiary) {
                        ToastUtils.getInstance(mainActivity)
                            .showToast("Cannot pick day in the future")
                    } else {
                        ToastUtils.getInstance(requireActivity())
                            .showToast(
                                if (isBirthDay) {
                                    getString(R.string.profile_invalid_age_toast)
                                } else {
                                    getString(R.string.invalid_starting_love_date)
                                }
                            )
                    }
                }
            }
        }
        binding!!.tvTitle.text = title
        setupMonthPicker()
        setupYearPicker()
        setupDayPicker()
    }

    private fun setupDayPicker() {
        binding!!.dayPicker.minValue = 1
    }

    private fun setupYearPicker() {
        binding!!.yearPicker.maxValue = if (isBirthDay) {
            Calendar.getInstance().get(Calendar.YEAR) - 16
        } else {
            Calendar.getInstance().get(Calendar.YEAR)
        }
        binding!!.yearPicker.minValue = 1900
        binding!!.yearPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            setupMaxDay()
        }
    }

    private fun setupMonthPicker() {
        val months = resources.getStringArray(R.array.months)
        binding!!.monthPicker.maxValue = 11
        binding!!.monthPicker.minValue = 0
        binding!!.monthPicker.wrapSelectorWheel = !isDiary
        binding!!.monthPicker.displayedValues = months
        binding!!.monthPicker.setOnValueChangedListener { numberPicker, i, i2 ->
            setupMaxDay()
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

    fun isValidDate(): Boolean {
        calendar = GregorianCalendar(
            binding!!.yearPicker.value,
            binding!!.monthPicker.value,
            binding!!.dayPicker.value
        )
        val minimumYear = if (isBirthDay) {
            MINIMUM_LOVING_AGE
        } else {
            0
        }
        return (calendar!!.get(Calendar.YEAR) < todayCalendar.get(Calendar.YEAR) - minimumYear) ||
                ((calendar!!.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) - minimumYear) &&
                        (calendar!!.get(Calendar.DAY_OF_YEAR) <= todayCalendar.get(Calendar.DAY_OF_YEAR)))
    }

    override fun onStart() {
        super.onStart()
        binding!!.monthPicker.value = calendar!!.get(Calendar.MONTH)
        binding!!.yearPicker.value =
            calendar!!.get(Calendar.YEAR).coerceAtMost(binding!!.yearPicker.maxValue)
        setupMaxDay()
        binding!!.dayPicker.value = calendar!!.get(Calendar.DAY_OF_MONTH)
    }

    interface OnDatePickerListener {
        fun onDatePicked(time: Long)
        fun onDismiss()
    }

    override fun getLayout(): Int {
        return R.layout.dialog_date_picker
    }

    companion object {
        private const val MINIMUM_LOVING_AGE = 16
        private const val KEY_TITLE = "Key_title"
        private const val KEY_IS_BIRTHDAY = "Key_BIRTHDAY"
        private const val KEY_IS_DIARY = "Key_Is_Diary"
        fun create(title: String, isBirthDay: Boolean): DatePickerDiaLog {
            val dialog = DatePickerDiaLog()
            val bundle = Bundle()
            bundle.putString(KEY_TITLE, title)
            bundle.putBoolean(KEY_IS_BIRTHDAY, isBirthDay)
            dialog.arguments = bundle
            return dialog
        }

        fun create(isDiary: Boolean, title: String): DatePickerDiaLog {
            val dialog = DatePickerDiaLog()
            val bundle = Bundle()
            bundle.putString(KEY_TITLE, title)
            bundle.putBoolean(KEY_IS_DIARY, isDiary)
            dialog.arguments = bundle
            return dialog
        }
    }

    override fun onDismiss() {
        dismiss()
        onDatePickerListener?.onDismiss()
    }
}
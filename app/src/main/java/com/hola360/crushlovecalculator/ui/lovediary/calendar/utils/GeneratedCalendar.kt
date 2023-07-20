package com.hola360.crushlovecalculator.ui.lovediary.calendar.utils

sealed class GeneratedCalendar<T> constructor(val status: GenerateCalendarStatus) {
    data class NewData<T>(val body: T) : GeneratedCalendar<T>(GenerateCalendarStatus.New)
    data class PreviousData<T>(val body: T) : GeneratedCalendar<T>(GenerateCalendarStatus.Previous)
    data class NextData<T>(val body: T) : GeneratedCalendar<T>(GenerateCalendarStatus.Next)
    class NoData<T> : GeneratedCalendar<T>(GenerateCalendarStatus.NoData)
}

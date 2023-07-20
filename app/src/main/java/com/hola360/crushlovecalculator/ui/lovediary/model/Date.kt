package com.hola360.crushlovecalculator.ui.lovediary.model

data class Date(
    var date: Int = 0,
    var year: Int = 0,
    var month: Int = 0,
    var isSelfMonthDate: Boolean = true,
    var weekDay: Int = -1,
    var hasDiary: Boolean = false,
    var isNowDate: Boolean = false,
    var isSelected: Boolean = false
) {
    constructor(month: Int, year: Int) : this() {
        this.month = month
        this.year = year
    }
}
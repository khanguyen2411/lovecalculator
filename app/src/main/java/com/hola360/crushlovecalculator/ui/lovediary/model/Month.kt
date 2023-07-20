package com.hola360.crushlovecalculator.ui.lovediary.model

data class Month(
    var month: Int = -1,
    var year: Int = -1,
    var days: MutableList<Date> = mutableListOf()
)

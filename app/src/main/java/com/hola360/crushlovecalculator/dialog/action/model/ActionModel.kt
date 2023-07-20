package com.hola360.crushlovecalculator.dialog.action.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ActionModel(val icon: Int, val title: String, val selectMode: Boolean,var selected: Boolean = false) : Parcelable

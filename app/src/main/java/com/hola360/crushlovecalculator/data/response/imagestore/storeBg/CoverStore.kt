package com.hola360.crushlovecalculator.data.response.imagestore.storeBg

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CoverStore(
    val full: String,
    val thumb: String
) : Parcelable
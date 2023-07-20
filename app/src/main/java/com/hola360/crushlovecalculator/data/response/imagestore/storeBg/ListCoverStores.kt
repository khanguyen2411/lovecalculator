package com.hola360.crushlovecalculator.data.response.imagestore.storeBg

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ListCoverStores(
    val list: List<CoverStore>,
    val title: String
) : Parcelable

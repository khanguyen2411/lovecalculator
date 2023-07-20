package com.hola360.crushlovecalculator.dialog.download.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DownloadModel(
    val itemId: String?,
    val title: String,
    val url: String,
    val thumb: String,
    val outputPath: String
) : Parcelable
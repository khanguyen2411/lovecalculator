package com.hola360.crushlovecalculator.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Gallery(var itemId: Long, var path: String?, var uri: Uri?, var dateAdded:Long, var title: String?) : Parcelable {
    var bucketDisplayName:String?=null
    var isChecked = false
    var isEditMode = false
}
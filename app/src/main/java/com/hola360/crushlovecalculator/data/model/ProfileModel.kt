package com.hola360.crushlovecalculator.data.model

import android.os.Parcelable
import com.hola360.crushlovecalculator.dialog.nationdialog.NationModel
import kotlinx.android.parcel.Parcelize

@Parcelize
class ProfileModel:Parcelable {
    var avatarUrl:String?=null
    var name:String?=null
    var dateOfBirth:Long?=null
    var nation:NationModel?=null
    var job:String?= null
    var height:Float?=null
    var heightUnitCm:Boolean=true
    var weight:Float?=null
    var weightUnitKg:Boolean=true
}
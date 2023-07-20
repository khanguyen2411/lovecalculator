package com.hola360.crushlovecalculator.dialog.nationdialog

import com.google.gson.annotations.SerializedName

class NationInformation {
    @SerializedName("code")
    var code:String?= null
    @SerializedName("flag")
    var flag:String?= null
    @SerializedName("iso")
    var iso:Boolean= false
    @SerializedName("name")
    var name:String?=null
}
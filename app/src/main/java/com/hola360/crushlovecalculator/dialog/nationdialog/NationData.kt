package com.hola360.crushlovecalculator.dialog.nationdialog

import com.google.gson.annotations.SerializedName

class NationData {
    @SerializedName("nations")
    var nationInformation= mutableListOf<NationInformation>()
}
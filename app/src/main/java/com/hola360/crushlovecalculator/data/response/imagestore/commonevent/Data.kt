package com.hola360.crushlovecalculator.data.response.imagestore.commonevent

import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("data_apps")
    val dataApps: DataApps,
    @SerializedName("time")
    val time: Int,
    @SerializedName("token")
    val token: String
)
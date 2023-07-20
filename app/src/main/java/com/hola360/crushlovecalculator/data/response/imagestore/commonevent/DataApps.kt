package com.hola360.crushlovecalculator.data.response.imagestore.commonevent

import com.google.gson.annotations.SerializedName
import com.hola360.crushlovecalculator.data.model.event.EventModel

data class DataApps(
    @SerializedName("status")
    var status: Int,
    @SerializedName("msg")
    var message: String,
    @SerializedName("sub_url")
    var subUrl: String,
    @SerializedName("list")
    var list: MutableList<EventModel>?
)
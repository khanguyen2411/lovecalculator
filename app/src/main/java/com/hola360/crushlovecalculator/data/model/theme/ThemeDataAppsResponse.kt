package com.hola360.crushlovecalculator.data.model.theme

import com.google.gson.annotations.SerializedName

data class ThemeDataAppsResponse(
    @SerializedName("data_apps")
    val themeListData: ThemeListData,
    @SerializedName("time")
    val time: Int,
    @SerializedName("token")
    val token: String
)
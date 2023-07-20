package com.hola360.crushlovecalculator.data.model.theme

import com.google.gson.annotations.SerializedName

data class ThemeListData(
    @SerializedName("status")
    val status: Int,
    @SerializedName("msg")
    val msg: String,
    @SerializedName("sub_url")
    val subUrl: String,
    @SerializedName("lists")
    val themeModelList: MutableList<ThemeModel>
)
